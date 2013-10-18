package com.campusrecruit.net;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.campusrecruit.app.AppConfig;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.app.AppException;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSSection;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Company;
import com.campusrecruit.bean.Notice;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.Result;
import com.campusrecruit.bean.URLs;
import com.campusrecruit.bean.Update;
import com.campusrecruit.bean.User;
import com.campusrecruit.bean.UserMessage;
import com.campusrecruit.bean.UserPreference;

import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.WebIconDatabase.IconListener;

public class NetApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	/*
	 * private final static int TIMEOUT_CONNECTION = 20000; private final static
	 * int TIMEOUT_SOCKET = 20000;
	 */
	private final static int TIMEOUT_CONNECTION = 3000;
	private final static int TIMEOUT_SOCKET = 3000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}

	private static String getCookie(AppContext appContext) {
		if (appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}

	private static String getUserAgent(AppContext appContext) {
		if (appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/' + appContext.getPackageInfo().versionName + '_'
					+ appContext.getPackageInfo().versionCode);// App版本
			ua.append("/Android");// 手机系统平台
			ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
			ua.append("/" + android.os.Build.MODEL); // 手机型号
			ua.append("/" + appContext.getAppId());// 客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

	private static GetMethod getHttpGet(String url, String cookie,
			String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URLs.getHost());
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		Log.i("test", "httpget");
		return httpGet;
	}

	private static PostMethod getHttpPost(String url, String cookie,
			String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.getHost());
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}

	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

	public static boolean pingHost(AppContext appContext, String url)
			throws IOException {
		HttpClient httpClient = null;
		String userAgent = getUserAgent(appContext);
		String cookie = getCookie(appContext);
		httpClient = getHttpClient();
		GetMethod httpGet = getHttpGet(url, cookie, userAgent);
		int statusCode = httpClient.executeMethod(httpGet);
		if (statusCode == HttpStatus.SC_OK) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 * @throws IOException
	 */
	private static String httpGet(AppContext appContext, String url)
			throws AppException {
		// System.out.println("get_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;

		InputStream responseStream = null;
		String responseBody = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				Log.i("test", String.format("status code %d", statusCode));
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseStream = httpGet.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(responseStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				responseBody = sb.toString();
				// System.out.println("XMLDATA=====>"+responseBody);
				return responseBody;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题

				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常

				throw AppException.network(e);
			} finally {
				// 释放连接
				try {
					if (responseStream != null)
						responseStream.close();
				} catch (IOException e) {
				}
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return null;
	}

	private static void httpGetNotReply(AppContext appContext, String url)
			throws AppException {
		// System.out.println("get_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;

		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				Log.i("test", String.format("status code %d", statusCode));
				if (statusCode == HttpStatus.SC_OK) {
					return;
				} else {
					time++;
				}
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题

				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常

				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
	}

	private static String httpGetStr(AppContext appContext, String url)
			throws AppException {
		try {
			Log.i("url", url);
			return new JSONObject(httpGet(appContext, url))
					.getString(AppConfig.JSON_IDENTIFIER);
		} catch (JSONException e) {

			throw AppException.json(e);
		}
	}

	private static JSONObject httpGetJson(AppContext appContext, String url)
			throws AppException {
		try {
			Log.i("url", url);
			JSONObject object = new JSONObject(httpGet(appContext, url));
			return object.getJSONObject(AppConfig.JSON_IDENTIFIER);
		} catch (NullPointerException ne) {
			return null;
		} catch (JSONException e) {

			throw AppException.json(e);
		}
	}

	private static JSONArray httpGetJsonArray(AppContext appContext, String url)
			throws AppException {
		try {
			Log.i("url", url);
			return new JSONObject(httpGet(appContext, url))
					.getJSONArray(AppConfig.JSON_IDENTIFIER);
		} catch (NullPointerException ne) {
			return null;
		} catch (JSONException e) {

			throw AppException.json(e);
		}
	}

	/**
	 * 公用post方法
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static String httpPost(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {
		Log.i("url", url);
		// System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;

		// post表单参数处理
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
				// System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {

				}
				// System.out.println("post_key_file==> "+file);
			}

		InputStream responseStream = null;
		String responseBody = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);
				httpPost.setRequestEntity(new MultipartRequestEntity(parts,
						httpPost.getParams()));
				int statusCode = httpClient.executeMethod(httpPost);
				Log.i("status", statusCode + "");
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				} else if (statusCode == HttpStatus.SC_OK) {
					Cookie[] cookies = httpClient.getState().getCookies();
					String tmpcookies = "";
					for (Cookie ck : cookies) {
						tmpcookies += ck.toString() + ";";
					}
					// 保存cookie
					if (appContext != null && tmpcookies != "") {
						appContext.setProperty("cookie", tmpcookies);
						appCookie = tmpcookies;
					}
				}
				responseStream = httpPost.getResponseBodyAsStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(responseStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				responseBody = sb.toString();
				// System.out.println("XMLDATA=====>"+responseBody);
				return responseBody;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题

				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常

				throw AppException.network(e);
			} finally {
				// 释放连接
				try {
					if (responseStream != null)
						responseStream.close();
				} catch (IOException e) {
				}
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

		/*
		 * responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		 * if(responseBody.contains("result") &&
		 * responseBody.contains("errorCode") &&
		 * appContext.containsProperty("user.uid")){ try { Result res =
		 * Result.parse(new ByteArrayInputStream(responseBody.getBytes()));
		 * if(res.getErrorCode() == 0){ appContext.Logout();
		 * appContext.getUnLoginHandler().sendEmptyMessage(1); } } catch
		 * (Exception e) { } }
		 */
		Log.i("test", "test");
		return null;
	}

	private static String httpPostJsonStr(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {
		try {
			Log.i("url", url);
			return new JSONObject(httpPost(appContext, url, params, files))
					.getString(AppConfig.JSON_IDENTIFIER);
		} catch (JSONException e) {

			throw AppException.json(e);
		}
	}

	/**
	 * 公用post方法
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static void httpPostNoReply(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {
		Log.i("url", url);
		// System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		PostMethod httpPost = null;
		Log.i("url", "excute");
		// post表单参数处理
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
				// System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {

				}
				// System.out.println("post_key_file==> "+file);
			}

		int time = 0;
		do {
			try {
				Log.i("url", "excute");
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);
				httpPost.setRequestEntity(new MultipartRequestEntity(parts,
						httpPost.getParams()));
				int statusCode = httpClient.executeMethod(httpPost);
				Log.i("test", statusCode + " ");
				if (statusCode != HttpStatus.SC_OK) {
					time++;
					throw AppException.http(statusCode);
				} else if (statusCode == HttpStatus.SC_OK) {
					Cookie[] cookies = httpClient.getState().getCookies();
					String tmpcookies = "";
					for (Cookie ck : cookies) {
						tmpcookies += ck.toString() + ";";
					}
					// 保存cookie
					if (appContext != null && tmpcookies != "") {
						appContext.setProperty("cookie", tmpcookies);
						appCookie = tmpcookies;
					}
					return;
				}
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题

				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常

				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);

	}

	private static JSONObject httpPostJson(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException, JSONException {
		return new JSONObject(httpPost(appContext, url, params, files))
				.getJSONObject(AppConfig.JSON_IDENTIFIER);
	}

	private static JSONArray httpPostJsonArray(AppContext appContext,
			String url, Map<String, Object> params, Map<String, File> files)
			throws AppException, JSONException {
		return new JSONObject(httpPost(appContext, url, params, files))
				.getJSONArray(AppConfig.JSON_IDENTIFIER);
	}

	/**
	 * post请求URL
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 * @throws IOException
	 * @throws
	 */
	/*
	 * private static Result httpPostJson(AppContext appContext, String url,
	 * Map<String, Object> params, Map<String,File> files) throws AppException,
	 * IOException { return Result.parse(_post(appContext, url, params, files));
	 * }
	 */

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(final String uid) throws AppException {
		// System.out.println("image_url==> "+url);
		String url = _MakeURL(URLs.USER_FACE, new HashMap<String, Object>() {
			{
				put("userID", uid);
			}
		});

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
				bitmap = BitmapFactory.decodeStream(inStream);
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题

				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常

				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return bitmap;
	}

	/**
	 * 检查版本更新
	 * 
	 * @param url
	 * @return
	 */
	public static Update checkVersion(AppContext appContext)
			throws AppException {
		try {
			return Update.parse(httpGetJson(appContext, URLs.UPDATE_VERSION));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 更新用户头像
	 * 
	 * @param appContext
	 * @param uid
	 *            当前用户uid
	 * @param portrait
	 *            新上传的头像
	 * @return
	 * @throws AppException
	 */
	public static void updatePortrait(AppContext appContext, String uid,
			File portrait) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);

		Map<String, File> files = new HashMap<String, File>();
		files.put("portrait", portrait);

		try {
			httpPostNoReply(appContext, URLs.USER_UPDATE_PORTRAIT, params,
					files);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 登录， 自动处理cookie
	 * 
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, final String userName,
			final String pwd) throws AppException {
		String encodeUserName = null;
		try {
			encodeUserName = URLEncoder.encode(userName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			Log.i("oops", "编码错误");
			e1.printStackTrace();
			return null;
		}
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("userName", encodeUserName);
		paramsMap.put("pwd", pwd);
		String newUrl = _MakeURL(URLs.USER_LOGIN, paramsMap);
		Log.i("login", newUrl);
		try {
			JSONObject jsonObject = httpGetJson(appContext, newUrl);
			if (jsonObject == null)
				return null;
			return User.parseDetail(jsonObject);
		} catch (Exception e) {
			if (e instanceof AppException) {
				if (((AppException) e).getType() == AppException.TYPE_JSON) {
					return null;
				}
			}
			throw AppException.network(e);
		}
	}

	/**
	 * 登录， 自动处理cookie
	 * 
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static int register(AppContext appContext, final String uid,
			final String userName, final String pwd) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		String encodeUserName = null;
		try {
			encodeUserName = URLEncoder.encode(userName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			Log.i("oops", "编码错误");
			e1.printStackTrace();
			return -1;
		}
		params.put("userName", encodeUserName);
		Log.i("test", encodeUserName);
		params.put("pwd", pwd);
		Log.i("db", uid);
		params.put("userID", uid);
		String url = _MakeURL(URLs.USER_REGISTER, params);
		try {
			return Integer.parseInt(httpGetStr(appContext, url));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 我的个人资料
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static User getUserInfo(AppContext appContext, final String uid)
			throws AppException {
		String newUrl = _MakeURL(URLs.USER_INFO, new HashMap<String, Object>() {
			{
				put("userID", uid);
			}
		});
		try {
			User user = User.parseDetail(httpGetJson(appContext, newUrl));
			if (user != null) {
				user.setUid(uid);
			}
			return user;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 我的推送信息偏好设置
	 * 
	 * @param appContext
	 * @param uid
	 * @param preference
	 * @return
	 * @throws AppException
	 */
	public static void setUserInfo(AppContext appContext, String uid,
			String email, String major, String schoolName, int gender)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);
		params.put("email", email);
		params.put("major", major);
		params.put("school", schoolName);
		params.put("gender", gender);
		try {
			httpPostNoReply(appContext, URLs.USER_SET_INFO, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 我的推送信息偏好设置
	 * 
	 * @param appContext
	 * @param uid
	 * @param preference
	 * @return
	 * @throws AppException
	 */
	public static void setPreference(AppContext appContext, String uid,
			String industry, String property, String source, String province)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);
		params.put("province", province);
		params.put("industry", industry);
		params.put("type", property);
		params.put("source", source);
		Log.i("url", uid);
		try {
			httpPostNoReply(appContext, URLs.USER_SET_PREFERENCE, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 我的推送信息偏好设置
	 * 
	 * @param appContext
	 * @param uid
	 * @param preference
	 * @return
	 * @throws AppException
	 */
	public static void setPreference(AppContext appContext, String uid,
			String notifyType) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);
		params.put("notifyType", notifyType);
		String newUrl = _MakeURL(URLs.USER_SET_PREFERENCE, params);
		Log.i("test", newUrl);
		try {
			httpGetNotReply(appContext, newUrl);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获得公司详细信息
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static Company getCompanyInfo(AppContext appContext,
			final int companyID) throws AppException {
		String newUrl = _MakeURL(URLs.COMPANY_DETAIL,
				new HashMap<String, Object>() {
					{
						put("companyID", companyID);
					}
				});
		try {
			return Company.parseDetail(httpGetJson(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取校园招聘信息列表
	 * 
	 * @param appContext
	 * @param uid
	 *            用户id
	 * @param pageIndex
	 *            页数
	 * @param orderby
	 *            排序方式
	 * @return
	 * @throws AppException
	 */
	public static List<Recruit> getRecruitList(AppContext appContext,
			final String uid, final int pageIndex, final int orderby,
			final boolean famous) throws AppException {
		String newUrl = _MakeURL(URLs.RECRUIT_LIST,
				new HashMap<String, Object>() {
					{
						put("userID", uid);
						put("pageIndex", pageIndex);
						put("order", orderby);
						if (famous)
							put("famous", 1);
						else
							put("famous", 0);
					}
				});
		try {
			JSONArray recruitArray = httpGetJsonArray(appContext, newUrl);
			ArrayList<Recruit> recruitList = new ArrayList<Recruit>();
			for (int i = 0; i < recruitArray.length(); i++) {
				recruitList
						.add(Recruit.parseBase(recruitArray.getJSONObject(i)));
			}
			return recruitList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取校园招聘信息列表
	 * 
	 * @param appContext
	 * @param uid
	 *            用户id
	 * @param pageIndex
	 *            页数
	 * @param orderby
	 *            排序方式
	 * @return
	 * @throws AppException
	 */
	public static List<Recruit> getRecruitList(AppContext appContext,
			final String uid, final int pageIndex, final int orderby,
			final boolean famous, final String province, final String industry,
			final String type, final String source) throws AppException {
		String newUrl = _MakeURL(URLs.RECRUIT_LIST,
				new HashMap<String, Object>() {
					{
						put("userID", uid);
						put("pageIndex", pageIndex);
						put("order", orderby);
						if (famous)
							put("famous", 1);
						else
							put("famous", 0);
						if (province != null) {
							put("province", province);
						}
						if (industry != null) {
							put("industry", industry);
						}
						if (type != null) {
							put("type", type);
						}
						if (source != null) {
							put("source", source);
						}
					}
				});
		try {
			JSONArray recruitArray = httpGetJsonArray(appContext, newUrl);
			ArrayList<Recruit> recruitList = new ArrayList<Recruit>();
			for (int i = 0; i < recruitArray.length(); i++) {
				recruitList
						.add(Recruit.parseBase(recruitArray.getJSONObject(i)));
			}
			return recruitList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 按公司名称搜索校园招聘信息
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static List<Recruit> searchRecruitList(AppContext appContext,
			final String companyName) throws AppException {
		String newUrl;
		String encodedCompanyName = null;
		try {
			encodedCompanyName = URLEncoder.encode(companyName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			Log.i("oops", "编码错误");
			e1.printStackTrace();
			return null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyName", encodedCompanyName);
		newUrl = _MakeURL(URLs.RECRUIT_SEARCH, params);

		try {
			JSONArray recruitArray = httpGetJsonArray(appContext, newUrl);
			List<Recruit> recruitList = new ArrayList<Recruit>();
			if (recruitArray == null) {
				Log.i("test", "!!!!!");
				return recruitList;
			}
			Log.i("test", recruitArray.length() + "!!!");
			for (int i = 0; i < recruitArray.length(); i++) {
				recruitList
						.add(Recruit.parseBase(recruitArray.getJSONObject(i)));
			}
			return recruitList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static Recruit getRecruitProcessInfo(AppContext appContext,
			final int recruitID) throws AppException {
		String newUrl = _MakeURL(URLs.RECRUIT_JOB_PROCESS_INFO,
				new HashMap<String, Object>() {
					{
						put("recruitID", recruitID);
					}
				});
		try {
			return Recruit.parseRecruitProcess(httpGetJson(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static Recruit getRecruitDetail(AppContext appContext,
			final int recruitID) throws AppException {
		String newUrl = _MakeURL(URLs.RECRUIT_DETAIL,
				new HashMap<String, Object>() {
					{
						put("recruitID", recruitID);
					}
				});
		try {
			return Recruit.parseDetail(httpGetJson(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static Recruit getRecruitSimpleDetail(AppContext appContext,
			final int recruitID) throws AppException {
		String newUrl = _MakeURL(URLs.RECRUIT_SIMPLE_DETAIL,
				new HashMap<String, Object>() {
					{
						put("recruitID", recruitID);
					}
				});
		try {
			return Recruit.parseDetailSimple(httpGetJson(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取招聘职位描述信息
	 * 
	 * @param appContext
	 * @param uid
	 * @param careerTalkID
	 * @return
	 * @throws AppException
	 */
	public static Recruit getRecruitDescriptionInfo(AppContext appContext,
			final int recruitID) throws AppException {
		String newUrl = _MakeURL(URLs.RECRUIT_JOB_DESCRIPTION,
				new HashMap<String, Object>() {
					{
						put("recruitID", recruitID);
					}
				});
		try {
			return Recruit.parseRecruitProcess(httpGetJson(appContext, newUrl));

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 按公司名称搜索宣讲会信息列表
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static List<CareerTalk> searchCareerTalkList(AppContext appContext,
			final String companyName) throws AppException {
		String encodedCompanyName = null;
		try {
			encodedCompanyName = URLEncoder.encode(companyName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			Log.i("oops", "编码错误");
			e1.printStackTrace();
			return null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyName", encodedCompanyName);
		String newUrl = _MakeURL(URLs.CAREERTALK_SEARCH, params);
		try {
			List<CareerTalk> careerTalkList = new ArrayList<CareerTalk>();
			JSONArray careerArray = httpGetJsonArray(appContext, newUrl);
			if (careerArray == null)
				return null;
			for (int i = 0; i < careerArray.length(); i++) {
				careerTalkList.add(CareerTalk.parseBase(careerArray
						.getJSONObject(i)));
			}
			return careerTalkList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;

			throw AppException.network(e);
		}
	}

	/**
	 * 获取宣讲会信息列表
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static List<CareerTalk> getCareerTalkList(AppContext appContext,
			final String uid, final int pageIndex, final int orderby,
			final boolean famous) throws AppException {
		String newUrl = _MakeURL(URLs.CAREERTALK_LIST,
				new HashMap<String, Object>() {
					{
						// put("userID", uid);
						put("userID", uid);
						put("pageIndex", pageIndex);
						put("order", orderby);
						if (famous)
							put("famous", 1);
						else
							put("famous", 0);
					}
				});
		try {
			List<CareerTalk> careerTalkList = new ArrayList<CareerTalk>();
			JSONArray careerArray = httpGetJsonArray(appContext, newUrl);
			for (int i = 0; i < careerArray.length(); i++) {
				careerTalkList.add(CareerTalk.parseBase(careerArray
						.getJSONObject(i)));
			}
			return careerTalkList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static List<CareerTalk> getCareerTalkList(AppContext appContext,
			final String uid, final int pageIndex, final int orderby,
			final boolean famous, final String province, final String industry,
			final String type, final String school) throws AppException {
		String encodedSchool = null;
		if (school != null) {
			try {
				encodedSchool = URLEncoder.encode(school, "utf-8");
			} catch (UnsupportedEncodingException e1) {
				return null;
			}
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);
		params.put("pageIndex", pageIndex);
		params.put("order", orderby);
		if (famous)
			params.put("famous", 1);
		else
			params.put("famous", 0);
		if (province != null) {
			params.put("province", province);
		}
		if (industry != null) {
			params.put("industry", industry);
		}
		if (type != null) {
			params.put("type", type);
		}
		if (school != null) {
			params.put("school", encodedSchool);
		}
		String newUrl = _MakeURL(URLs.CAREERTALK_LIST,
				params);
		try {
			List<CareerTalk> careerTalkList = new ArrayList<CareerTalk>();
			JSONArray careerArray = httpGetJsonArray(appContext, newUrl);
			for (int i = 0; i < careerArray.length(); i++) {
				careerTalkList.add(CareerTalk.parseBase(careerArray
						.getJSONObject(i)));
			}
			return careerTalkList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取宣讲会介绍信息
	 * 
	 * @param appContext
	 * @param uid
	 * @param careerTalkID
	 * @return
	 * @throws AppException
	 */
	public static CareerTalk getCareerTalkDetail(AppContext appContext,
			final int careerTalkID) throws AppException {
		String newUrl = _MakeURL(URLs.CAREERTALK_DETAIL,
				new HashMap<String, Object>() {
					{
						put("careerTalkID", careerTalkID);
					}
				});
		try {
			return CareerTalk.parseBase(httpGetJson(appContext, newUrl));

		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 按公司名称搜索讨论组信息
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static List<BBSSection> searchBBSSectionList(AppContext appContext,
			final String companyName) throws AppException {
		String encodedCompanyName = null;
		try {
			encodedCompanyName = URLEncoder.encode(companyName, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			Log.i("oops", "编码错误");
			e1.printStackTrace();
			return null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyName", encodedCompanyName);
		String newUrl = _MakeURL(URLs.BBSSECTION_LIST, params);
		try {
			List<BBSSection> careerTalkList = new ArrayList<BBSSection>();
			JSONArray sectionArray = httpGetJsonArray(appContext, newUrl);
			if (sectionArray == null)
				return careerTalkList;
			for (int i = 0; i < sectionArray.length(); i++) {
				careerTalkList.add(BBSSection.parseBase(sectionArray
						.getJSONObject(i)));
			}
			return careerTalkList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取关注讨论组列表
	 * 
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<BBSSection> getBBSSectionList(
			AppContext appContext, final String uid, final int pageIndex,
			final int orderby, final int flag) throws AppException {
		String newUrl = _MakeURL(URLs.BBSSECTION_LIST,
				new HashMap<String, Object>() {
					{
						if (uid != null) {
							put("userID", uid);
						}
						put("pageIndex", pageIndex);
						put("order", orderby);
						put("flag", flag);
					}
				});
		try {
			ArrayList<BBSSection> bbsSectionList = new ArrayList<BBSSection>();
			JSONArray bbsSectionArray = httpGetJsonArray(appContext, newUrl);
			for (int i = 0; i < bbsSectionArray.length(); i++) {
				bbsSectionList.add(BBSSection.parseBase(bbsSectionArray
						.getJSONObject(i)));
			}
			return bbsSectionList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 添加新主题
	 * 
	 * @param appContext
	 * @param topic
	 * @return
	 * @throws AppException
	 */
	public static int newBBSTopic(AppContext appContext, int sectionID,
			BBSTopic topic) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sectionID", sectionID);
		params.put("userID", topic.getUserID());
		params.put("title", topic.getTitle());
		params.put("body", topic.getBody());
		try {
			return Integer.parseInt(httpPostJsonStr(appContext,
					URLs.BBSTOPIC_NEW, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 添加新主题
	 * 
	 * @param appContext
	 * @param topic
	 * @return
	 * @throws AppException
	 */
	public static int newBBSTopicByCompanyID(AppContext appContext,
			int companyID, BBSTopic topic) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("companyID", companyID);
		params.put("userID", topic.getUserID());
		params.put("title", topic.getTitle());
		params.put("body", topic.getBody());
		try {
			return Integer.parseInt(httpPostJsonStr(appContext,
					URLs.BBSTOPIC_NEW, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取讨论组主题列表
	 * 
	 * @param appContext
	 * @param sectionID
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<BBSTopic> getBBSTopicList(AppContext appContext,
			final String uid, final int sectionID, final int pageIndex)
			throws AppException {
		String newUrl = _MakeURL(URLs.BBSTOPIC_LIST,
				new HashMap<String, Object>() {
					{
						put("sectionID", sectionID);
						put("pageIndex", pageIndex);
						put("userID", uid);
					}
				});
		try {
			JSONArray bbsTopicArray = httpGetJsonArray(appContext, newUrl);
			ArrayList<BBSTopic> bbsTopicList = new ArrayList<BBSTopic>();
			for (int i = 0; i < bbsTopicArray.length(); i++) {
				bbsTopicList.add(BBSTopic.parseBase(bbsTopicArray
						.getJSONObject(i)));
			}
			return bbsTopicList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取讨论组主题列表通过公司名称
	 * 
	 * @param appContext
	 * @param sectionID
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<BBSTopic> getBBSTopicListByCompanyID(
			AppContext appContext, final String uid, final int companyID,
			final int pageIndex) throws AppException {
		String newUrl = _MakeURL(URLs.BBSTOPIC_LIST,
				new HashMap<String, Object>() {
					{
						put("companyID", companyID);
						put("pageIndex", pageIndex);
						put("userID", uid);
					}
				});
		try {
			JSONArray bbsTopicArray = httpGetJsonArray(appContext, newUrl);
			ArrayList<BBSTopic> bbsTopicList = new ArrayList<BBSTopic>();
			for (int i = 0; i < bbsTopicArray.length(); i++) {
				bbsTopicList.add(BBSTopic.parseBase(bbsTopicArray
						.getJSONObject(i)));
			}
			return bbsTopicList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取讨论组主题列表
	 * 
	 * @param appContext
	 * @param sectionID
	 * @return
	 * @throws AppException
	 */
	public static BBSTopic getBBSTopicDetail(AppContext appContext,
			final int topicID) throws AppException {
		String newUrl = _MakeURL(URLs.BBSTOPIC_DETAIL,
				new HashMap<String, Object>() {
					{
						put("topicID", topicID);
					}
				});
		try {
			return BBSTopic.parseDetail(httpGetJson(appContext, newUrl));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取讨论组主题回帖列表
	 * 
	 * @param appContext
	 * @param topicID
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<BBSReply> getBBSReplyList(AppContext appContext,
			final int topicID, final int pageIndex) throws AppException {
		Log.i("td", "getReplyList");
		String newUrl = _MakeURL(URLs.BBSREPLY_LIST,
				new HashMap<String, Object>() {
					{
						put("topicID", topicID);
						put("pageIndex", pageIndex);
					}
				});
		Log.i("td", newUrl);
		try {
			ArrayList<BBSReply> bbsReplyList = new ArrayList<BBSReply>();
			JSONArray bbsReplyArray = httpGetJsonArray(appContext, newUrl);
			for (int i = 0; i < bbsReplyArray.length(); i++) {
				bbsReplyList.add(BBSReply.parseDetail(bbsReplyArray
						.getJSONArray(i)));
			}
			return bbsReplyList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取用户消息列表
	 * 
	 * @param appContext
	 * @param userID
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<BBSTopic> getUserTopicList(AppContext appContext,
			final String userID) throws AppException {
		String newUrl = _MakeURL(URLs.USER_TOPIC_LIST,
				new HashMap<String, Object>() {
					{
						put("userID", userID);
					}
				});
		try {
			ArrayList<BBSTopic> topicList = new ArrayList<BBSTopic>();
			JSONArray topicJsonArray = httpGetJsonArray(appContext, newUrl);
			if (topicJsonArray == null) {
				return null;
			}
			for (int i = 0; i < topicJsonArray.length(); i++) {
				topicList.add(BBSTopic.parseBase(topicJsonArray
						.getJSONObject(i)));
			}
			return topicList;
		} catch (Exception e) {
			Log.i("contact", "get user contact bug");
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取用户消息列表
	 * 
	 * @param appContext
	 * @param userID
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<UserMessage> getUserContactList(
			AppContext appContext, final String userID) throws AppException {
		String newUrl = _MakeURL(URLs.USER_CONTACT_LIST,
				new HashMap<String, Object>() {
					{
						put("userID", userID);
					}
				});
		try {
			ArrayList<UserMessage> messageList = new ArrayList<UserMessage>();
			JSONArray messageJsonArray = httpGetJsonArray(appContext, newUrl);
			if (messageJsonArray == null) {
				return null;
			}
			for (int i = 0; i < messageJsonArray.length(); i++) {
				messageList.add(UserMessage.parseContact(messageJsonArray
						.getJSONObject(i)));
			}
			return messageList;
		} catch (Exception e) {
			Log.i("contact", "get user contact bug");
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取用户消息列表
	 * 
	 * @param appContext
	 * @param userID
	 * @param lastTime
	 *            俩人最后交互时间
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<UserMessage> getUserMessageList(
			AppContext appContext, final String userID, final String toUserID,
			final String lastTime) throws AppException {
		String encodedlastTime = null;
		if (lastTime != null) {
			try {
				encodedlastTime = URLEncoder.encode(lastTime, "utf-8");
			} catch (UnsupportedEncodingException e1) {
				Log.i("oops", "编码错误");
				e1.printStackTrace();
				return null;
			}
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", userID);
		params.put("otherUserID", toUserID);
		if (lastTime != null)
			params.put("createdTime", encodedlastTime);
		String newUrl = _MakeURL(URLs.USER_MESSAGE_LIST, params);
		Log.i("url", newUrl);
		try {
			ArrayList<UserMessage> messageList = new ArrayList<UserMessage>();
			JSONArray messageJsonArray = httpGetJsonArray(appContext, newUrl);
			if (messageJsonArray == null)
				return null;
			for (int i = 0; i < messageJsonArray.length(); i++) {
				messageList.add(UserMessage.parseBase(messageJsonArray
						.getJSONObject(i)));
			}
			return messageList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取他人回复我的内容
	 * 
	 * @param appContext
	 * @param topicID
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<BBSReply> getReplyByOtherList(
			AppContext appContext, final String userID) throws AppException {
		Log.i("td", "getReplyByOtherList");
		String newUrl = _MakeURL(URLs.BBSREPLY_BY_OTHER_LIST,
				new HashMap<String, Object>() {
					{
						put("userID", userID);
					}
				});
		Log.i("td", newUrl);
		try {
			ArrayList<BBSReply> bbsReplyList = new ArrayList<BBSReply>();
			JSONArray bbsReplyArray = httpGetJsonArray(appContext, newUrl);
			if (bbsReplyArray == null)
				return null;
			for (int i = 0; i < bbsReplyArray.length(); i++) {
				bbsReplyList.add(BBSReply.parseReplyByOther(bbsReplyArray
						.getJSONObject(i)));
			}
			return bbsReplyList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取我回复他人的内容
	 * 
	 * @param appContext
	 * @param topicID
	 * @return
	 * @throws AppException
	 */
	public static ArrayList<BBSReply> getReplyToOtherList(
			AppContext appContext, final String userID) throws AppException {
		Log.i("td", "getReplyToOtherList");
		String newUrl = _MakeURL(URLs.BBSREPLY_TO_OTHER_LIST,
				new HashMap<String, Object>() {
					{
						put("userID", userID);
					}
				});
		Log.i("td", newUrl);
		try {
			ArrayList<BBSReply> bbsReplyList = new ArrayList<BBSReply>();
			JSONArray bbsReplyArray = httpGetJsonArray(appContext, newUrl);
			if (bbsReplyArray == null)
				return null;
			for (int i = 0; i < bbsReplyArray.length(); i++) {
				bbsReplyList.add(BBSReply.parseReplyToOther(bbsReplyArray
						.getJSONObject(i)));
			}
			return bbsReplyList;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 删除用户回帖
	 * 
	 * @param appContext
	 * @param topic
	 * @return
	 * @throws AppException
	 */
	public static Result delBBSReply(AppContext appContext, int bbsReplyID)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bbsReplyID", bbsReplyID);
		try {
			return Result.parse(httpPostJson(appContext, URLs.BBSREPLY_DEL,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 新建评论
	 * 
	 * @param topicID
	 * @param uid
	 *            当前用户uid
	 * @param content
	 *            发表评论的内容
	 * @param replyid
	 *            表示被回复的单个评论id
	 * @param ouid
	 *            表示该评论的原始作者id
	 * @return
	 * @throws AppException
	 */
	public static BBSReply newBBSReply(AppContext appContext, int topicID,
			String uid, String content, int replyid) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("topicID", topicID);
		params.put("userID", uid);
		params.put("content", content);
		params.put("bbsReplyID", replyid);
		Log.i("test", String.format(
				"topicID %d, userID %s, content %s, replyId %d ", topicID, uid,
				content, replyid));
		try {
			return BBSReply.parseDetail(httpPostJsonArray(appContext,
					URLs.BBSREPLY_NEW, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 新建私信
	 * 
	 * @param uid
	 *            当前用户ID
	 * @param touid
	 *            发给用户ID
	 * @param title
	 *            私信主题
	 * @param content
	 *            私信内容
	 * @return
	 * @throws AppException
	 */

	public static int newPrivateMessage(AppContext appContext, String uid,
			String toUid, String content) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);
		params.put("toUserID", toUid);
		params.put("content", content);
		Log.i("test", uid + " : " + " : " + toUid + " : " + content);
		try {
			return Integer.parseInt((httpPostJsonStr(appContext,
					URLs.USER_MESSAGE_NEW, params, null)));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 参与校园招聘
	 * 
	 * @param appContext
	 * @param uid
	 * @param recruitid
	 * @return
	 * @throws AppException
	 */
	public static void joinRecruit(AppContext appContext, final String uid,
			final int recruitID, final boolean join) throws AppException {
		String newUrl = _MakeURL(URLs.JOIN_RECRUIT,
				new HashMap<String, Object>() {
					{
						// put("userID", "8aec50c040e3379d0140e337c31e0000");
						put("recruitID", recruitID);
						if (join) {
							put("join", 1);
						} else {
							put("join", 0);
						}
						put("userID", uid);
					}
				});
		try {
			httpGetNotReply(appContext, newUrl);
			Log.i("test", "join recruit no reply");
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 参与宣讲会
	 * 
	 * @param appContext
	 * @param uid
	 * @param careerTalkID
	 * @return
	 * @throws AppException
	 */
	public static void joinCareerTalk(AppContext appContext, final String uid,
			final int careerTalkID, final boolean join) throws AppException {
		String newUrl = _MakeURL(URLs.JOIN_CAREERTALK,
				new HashMap<String, Object>() {
					{
						// put("userID", "8aec50c040e3379d0140e337c31e0000");
						put("careerTalkID", careerTalkID);
						if (join) {
							put("join", 1);
						} else {
							put("join", 0);
						}
						put("userID", uid);
					}
				});
		try {
			httpGetNotReply(appContext, newUrl);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 参与宣讲会
	 * 
	 * @param appContext
	 * @param uid
	 * @param careerTalkID
	 * @return
	 * @throws AppException
	 */
	public static void cancelBBSSection(AppContext appContext,
			final String uid, final int sectionID) throws AppException {
		String newUrl = _MakeURL(URLs.CANCEL_BBSSECTION,
				new HashMap<String, Object>() {
					{
						put("sectionID", sectionID);
						put("userID", uid);
					}
				});
		Log.i("url", newUrl);
		try {
			httpGetNotReply(appContext, newUrl);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 点击宣讲会
	 * 
	 * @param appContext
	 * @param uid
	 * @param careerTalkID
	 * @return
	 * @throws AppException
	 */
	public static void clickCareerTalk(AppContext appContext,
			final int careerTalkID) throws AppException {
		String newUrl = _MakeURL(URLs.CAREERTALK_CLICK,
				new HashMap<String, Object>() {
					{
						put("careerTalkID", careerTalkID);
					}
				});
		try {
			httpGetNotReply(appContext, newUrl);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 参与讨论组
	 * 
	 * @param appContext
	 * @param uid
	 * @param companyName
	 * @return
	 * @throws AppException
	 */
	public static void joinBBSSection(AppContext appContext, final String uid,
			final String companyName, final boolean join) throws AppException {
		String newUrl = _MakeURL(URLs.JOIN_CAREERTALK,
				new HashMap<String, Object>() {
					{
						put("userID", uid);
						put("companyName", companyName);
						if (join) {
							put("join", 1);
						} else {
							put("join", 0);
						}
					}
				});
		try {
			httpGetNotReply(appContext, newUrl);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取用户通知信息
	 * 
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static Notice getUserNotice(AppContext appContext, String uid)
			throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);
		Log.i("url", uid);
		try {
			return Notice.parse(httpPostJsonArray(appContext, URLs.NOTICE_GET,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 清除用户通知信息
	 * 
	 * @param uid
	 * @return
	 * @throws AppException
	 */
	public static void clearUserNotice(AppContext appContext, String uid,
			int type) throws AppException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userID", uid);
		params.put("type", type);

		try {
			httpPostNoReply(appContext, URLs.NOTICE_CLEAR, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	// /**
	// * 获取动弹列表
	// * @param uid
	// * @param pageIndex
	// * @param pageSize
	// * @return
	// * @throws AppException
	// */
	// public static TweetList getTweetList(AppContext appContext, final String
	// uid, final int pageIndex, final int pageSize) throws AppException {
	// String newUrl = _MakeURL(URLs.TWEET_LIST, new HashMap<String, Object>(){{
	// put("uid", uid);
	// put("pageIndex", pageIndex);
	// put("pageSize", pageSize);
	// }});
	//
	// try{
	// return TweetList.parse(httpGet(appContext, newUrl));
	// }catch(Exception e){
	// if(e instanceof AppException)
	// throw (AppException)e;
	// throw AppException.network(e);
	// }
	// }
	//
	// /**
	// * 获取动弹详情
	// * @param tweet_id
	// * @return
	// * @throws AppException
	// */
	// public static Tweet getTweetDetail(AppContext appContext, final int
	// tweet_id) throws AppException {
	// String newUrl = _MakeURL(URLs.TWEET_DETAIL, new HashMap<String,
	// Object>(){{
	// put("id", tweet_id);
	// }});
	// try{
	// return Tweet.parse(httpGet(appContext, newUrl));
	// }catch(Exception e){
	// if(e instanceof AppException)
	// throw (AppException)e;
	// throw AppException.network(e);
	// }
	// }
	//
	// /**
	// * 发动弹
	// * @param Tweet-uid & msg & image
	// * @return
	// * @throws AppException
	// */
	// public static Result pubTweet(AppContext appContext, Tweet tweet) throws
	// AppException {
	// Map<String,Object> params = new HashMap<String,Object>();
	// params.put("uid", tweet.getAuthorId());
	// params.put("msg", tweet.getBody());
	//
	// Map<String, File> files = new HashMap<String, File>();
	// if(tweet.getImageFile() != null)
	// files.put("img", tweet.getImageFile());
	//
	// try{
	// return httpPostJson(appContext, URLs.TWEET_PUB, params, files);
	// }catch(Exception e){
	// if(e instanceof AppException)
	// throw (AppException)e;
	// throw AppException.network(e);
	// }
	// }
	//
	// /**
	// * 删除动弹
	// * @param uid
	// * @param tweetid
	// * @return
	// * @throws AppException
	// */
	// public static Result delTweet(AppContext appContext, String uid, int
	// tweetid) throws AppException {
	// Map<String,Object> params = new HashMap<String,Object>();
	// params.put("uid", uid);
	// params.put("tweetid", tweetid);
	//
	// try{
	// return httpPostJson(appContext, URLs.TWEET_DELETE, params, null);
	// }catch(Exception e){
	// if(e instanceof AppException)
	// throw (AppException)e;
	// throw AppException.network(e);
	// }
	// }
	//
	/**
	 * 更新用户头像
	 * 
	 * @param appContext
	 * @param uid
	 *            当前用户uid
	 * @param portrait
	 *            新上传的头像
	 * @return
	 * @throws AppException
	 */
	/*
	 * public static Result updatePortrait(AppContext appContext, String uid,
	 * File portrait) throws AppException { Map<String,Object> params = new
	 * HashMap<String,Object>(); params.put("uid", uid);
	 * 
	 * Map<String, File> files = new HashMap<String, File>();
	 * files.put("portrait", portrait);
	 * 
	 * try{ return httpPostJson(appContext, URLs.PORTRAIT_UPDATE, params,
	 * files); }catch(Exception e){ if(e instanceof AppException) throw
	 * (AppException)e; throw AppException.network(e); } }
	 */

	/**
	 * 获取用户信息个人专页（包含该用户的动态信息以及个人信息）
	 * 
	 * @param uid
	 *            自己的uid
	 * @param hisuid
	 *            被查看用户的uid
	 * @param hisname
	 *            被查看用户的用户名
	 * @param pageIndex
	 *            页面索引
	 * @param pageSize
	 *            每页读取的动态个数
	 * @return
	 * @throws AppException
	 */
	/*
	 * public static UserInformation information(AppContext appContext, String
	 * uid, int hisuid, String hisname, int pageIndex, int pageSize) throws
	 * AppException { Map<String,Object> params = new HashMap<String,Object>();
	 * params.put("uid", uid); params.put("hisuid", hisuid);
	 * params.put("hisname", hisname); params.put("pageIndex", pageIndex);
	 * params.put("pageSize", pageSize);
	 * 
	 * try{ return UserInformation.parse(_post(appContext,
	 * URLs.USER_INFORMATION, params, null)); }catch(Exception e){ if(e
	 * instanceof AppException) throw (AppException)e; throw
	 * AppException.network(e); } }
	 */

	/**
	 * 更新用户之间关系（加关注、取消关注）
	 * 
	 * @param uid
	 *            自己的uid
	 * @param hisuid
	 *            对方用户的uid
	 * @param newrelation
	 *            0:取消对他的关注 1:关注他
	 * @return
	 * @throws AppException
	 */
	/*
	 * public static Result updateRelation(AppContext appContext, String uid,
	 * int hisuid, int newrelation) throws AppException { Map<String,Object>
	 * params = new HashMap<String,Object>(); params.put("uid", uid);
	 * params.put("hisuid", hisuid); params.put("newrelation", newrelation);
	 * 
	 * try{ return Result.parse(_post(appContext, URLs.USER_UPDATERELATION,
	 * params, null)); }catch(Exception e){ if(e instanceof AppException) throw
	 * (AppException)e; throw AppException.network(e); } }
	 */

}
