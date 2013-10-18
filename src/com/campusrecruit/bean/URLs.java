package com.campusrecruit.bean;

import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.campusrecruit.common.StringUtils;

public class URLs implements Serializable {
	
	public static boolean isPrivate = false;
	public static String YIZHUANG_HOST = "lib.youionline.com:9080";
	public static String BYR_HOST = "10.108.210.59:8080";
	
//	public static String HOST = isPrivate ? BYR_HOST : YIZHUANG_HOST;
	
	
//	public static String HOST = "10.108.208.64:8080";
	public static String HTTP = "http://";
	public static String HTTPS = "https://";
	
	private static String URL_SPLITTER = "/";
	private static String URL_MOBILE = "mobile/";
	private static String URL_UNDERLINE = "_";
	private static String JSON = ".json";
	
	
	private final static String GLOBAL_URL_HOST = HTTP + YIZHUANG_HOST + URL_SPLITTER + URL_MOBILE;
	
	
//	private static String getUrlHost() = HTTP + HOST + URL_SPLITTER + URL_MOBILE;
	
	public static String getHost() {
		return isPrivate ? BYR_HOST : YIZHUANG_HOST;
	}
	
	private static String getUrlHost() {
		String HOST = isPrivate ? BYR_HOST : YIZHUANG_HOST;
		return HTTP + HOST + URL_SPLITTER + URL_MOBILE;
	}
	
	
	public static String NOTICE_GET = getUrlHost()+"notice" + JSON;
	public static String NOTICE_CLEAR = getUrlHost() +"noticeClear" + JSON;
	
	public static String USER_INFO = GLOBAL_URL_HOST+"user/get" + JSON;
	public static String USER_LOGIN = GLOBAL_URL_HOST+"user/login" + JSON;
	public static String USER_LOGOUT = GLOBAL_URL_HOST+"user/logout" + JSON;
	
	public static String USER_REGISTER = GLOBAL_URL_HOST+"user/register" + JSON;
	public static String USER_UPDATE_PORTRAIT = GLOBAL_URL_HOST + "user/portrait/update" + JSON;
	public static String BBSREPLY_BY_OTHER_LIST = GLOBAL_URL_HOST+"user/replies" + JSON;
	public static String BBSREPLY_TO_OTHER_LIST = GLOBAL_URL_HOST +"user/comments" + JSON;
	public static String USER_FACE = GLOBAL_URL_HOST + "user/portrait/download" + JSON;
	
	public static String USER_SET_INFO = GLOBAL_URL_HOST+"user/set" + JSON;
	public static String USER_SET_PREFERENCE = GLOBAL_URL_HOST+"user/preference/set" + JSON;
	public static String USER_SET_NOTIFY = GLOBAL_URL_HOST+"preference/setNotify" + JSON;
	
	public static String JOIN_CAREERTALK = GLOBAL_URL_HOST+"careerTalk/join" + JSON;
	public static String JOIN_RECRUIT = GLOBAL_URL_HOST+"recruit/join" + JSON;
	public static String CANCEL_BBSSECTION = GLOBAL_URL_HOST+"bbsSection/delete" + JSON;
	
	public static String BBSSECTION_LIST = GLOBAL_URL_HOST+"bbsSection/list" + JSON;
	public static String BBSSECTION_SEARCH = GLOBAL_URL_HOST+"bbsSection/search" + JSON;
	public static String BBSTOPIC_LIST = GLOBAL_URL_HOST+"bbsTopic/list" + JSON;
	public static String BBSTOPIC_NEW = GLOBAL_URL_HOST+"new/bbsTopic" + JSON;
	public static String BBSTOPIC_DETAIL = GLOBAL_URL_HOST + "bbsTopic/detail" + JSON;
	public static String BBSREPLY_LIST = GLOBAL_URL_HOST+"bbsReply/list" + JSON;
	public static String BBSREPLY_NEW = GLOBAL_URL_HOST+"new/bbsReply" + JSON ;
	public static String BBSREPLY_DEL = GLOBAL_URL_HOST+"del/bbsReply" + JSON;
	
	
	
	public static String USER_TOPIC_LIST = getUrlHost()+"user/topics" + JSON;
	public static String USER_CONTACT_LIST = getUrlHost()+"user/message/contacts" + JSON;
	public static String USER_MESSAGE_LIST = getUrlHost()+"user/message/list" + JSON;
	public static String USER_MESSAGE_NEW = getUrlHost()+"user/message/new" + JSON;
	
	public static String COMPANY_DETAIL = getUrlHost()+"company" + JSON;
	
	public static String CAREERTALK_LIST = getUrlHost()+"careerTalk/list" + JSON;
	public static String CAREERTALK_CLICK = getUrlHost()+"careerTalk/click" + JSON;
	public static String CAREERTALK_DETAIL = getUrlHost()+"careerTalk/detail" + JSON;
	public static String CAREERTALK_SEARCH = getUrlHost()+"careerTalk/search" + JSON;
	
	public static String RECRUIT_LIST = getUrlHost()+"recruit/list" + JSON;
	public static String RECRUIT_DETAIL = getUrlHost()+"recruit/detail" + JSON;
	public static String RECRUIT_SIMPLE_DETAIL = getUrlHost()+"recruit/simpledetail" + JSON;
	public static String RECRUIT_JOB_DESCRIPTION = getUrlHost()+"recruit/description" + JSON;
	public static String RECRUIT_JOB_PROCESS_INFO = getUrlHost()+"recruit/contact" + JSON;
	public static String RECRUIT_SEARCH = getUrlHost()+"recruit/search" + JSON;
	
	public static String UPDATE_VERSION = getUrlHost()+"AppVersion.json";
	
	
	
	public static int URL_OBJ_TYPE_OTHER = 0x000;
	public static int URL_OBJ_TYPE_NEWS = 0x001;
	public static int URL_OBJ_TYPE_SOFTWARE = 0x002;
	public static int URL_OBJ_TYPE_QUESTION = 0x003;
	public static int URL_OBJ_TYPE_ZONE = 0x004;
	public static int URL_OBJ_TYPE_BLOG = 0x005;
	public static int URL_OBJ_TYPE_TWEET = 0x006;
	public static int URL_OBJ_TYPE_QUESTION_TAG = 0x007;
	
	private int objId;
	private String objKey = "";
	private int objType;
	
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getObjKey() {
		return objKey;
	}
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}
	
	/**
	 * 转化URL为URLs实体
	 * @param path
	 * @return 不能转化的链接返回null
	 */
//	public static URLs parseURL(String path) {
//		if(StringUtils.isEmpty(path))return null;
//		path = formatURL(path);
//		URLs urls = null;
//		String objId = "";
//		try {
//			URL url = new URL(path);
//			//站内链接
//			if(url.getHost().contains(GLOBAL_URL_HOST)){
//				urls = new URLs();
//				//www
//				if(path.contains(URL_WWW_HOST )){
//					//新闻  www.oschina.net/news/27259/mobile-internet-market-is-small
//					if(path.contains(URL_TYPE_NEWS)){
//						objId = parseObjId(path, URL_TYPE_NEWS);
//						urls.setObjId(StringUtils.toInt(objId));
//						urls.setObjType(URL_OBJ_TYPE_NEWS);
//					}
//					//软件  www.oschina.net/p/jx
//					else if(path.contains(URL_TYPE_SOFTWARE)){
//						urls.setObjKey(parseObjKey(path, URL_TYPE_SOFTWARE));
//						urls.setObjType(URL_OBJ_TYPE_SOFTWARE);
//					}
//					//问答
//					else if(path.contains(URL_TYPE_QUESTION)){
//						//问答-标签  http://www.oschina.net/question/tag/python
//						if(path.contains(URL_TYPE_QUESTION_TAG)){
//							urls.setObjKey(parseObjKey(path, URL_TYPE_QUESTION_TAG));
//							urls.setObjType(URL_OBJ_TYPE_QUESTION_TAG);
//						}
//						//问答  www.oschina.net/question/12_45738
//						else{
//							objId = parseObjId(path, URL_TYPE_QUESTION);
//							String[] _tmp = objId.split(URL_UNDERLINE);
//							urls.setObjId(StringUtils.toInt(_tmp[1]));
//							urls.setObjType(URL_OBJ_TYPE_QUESTION);
//						}
//					}
//					//other
//					else{
//						urls.setObjKey(path);
//						urls.setObjType(URL_OBJ_TYPE_OTHER);
//					}
//				}
//				//my
//				else if(path.contains(URL_MY_HOST)){					
//					//博客  my.oschina.net/szpengvictor/blog/50879
//					if(path.contains(URL_TYPE_BLOG)){
//						objId = parseObjId(path, URL_TYPE_BLOG);
//						urls.setObjId(StringUtils.toInt(objId));
//						urls.setObjType(URL_OBJ_TYPE_BLOG);
//					}
//					//动弹  my.oschina.net/dong706/tweet/612947
//					else if(path.contains(URL_TYPE_TWEET)){
//						objId = parseObjId(path, URL_TYPE_TWEET);
//						urls.setObjId(StringUtils.toInt(objId));
//						urls.setObjType(URL_OBJ_TYPE_TWEET);
//					}
//					//个人专页  my.oschina.net/u/12
//					else if(path.contains(URL_TYPE_ZONE)){
//						objId = parseObjId(path, URL_TYPE_ZONE);
//						urls.setObjId(StringUtils.toInt(objId));
//						urls.setObjType(URL_OBJ_TYPE_ZONE);
//					}
//					else{
//						//另一种个人专页  my.oschina.net/dong706
//						int p = path.indexOf(URL_MY_HOST+URL_SPLITTER) + (URL_MY_HOST+URL_SPLITTER).length();
//						String str = path.substring(p);
//						if(!str.contains(URL_SPLITTER)){
//							urls.setObjKey(str);
//							urls.setObjType(URL_OBJ_TYPE_ZONE);
//						}
//						//other
//						else{
//							urls.setObjKey(path);
//							urls.setObjType(URL_OBJ_TYPE_OTHER);
//						}
//					}
//				}
//				//other
//				else{
//					urls.setObjKey(path);
//					urls.setObjType(URL_OBJ_TYPE_OTHER);
//				}
//			}
//		} catch (Exception e) {
//			
//			urls = null;
//		}
//		return urls;
//	}

	/**
	 * 解析url获得objId
	 * @param path
	 * @param url_type
	 * @return
	 */
	private static String parseObjId(String path, String url_type){
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains(URL_SPLITTER)){
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		}else{
			objId = str;
		}
		return objId;
	}
	
	/**
	 * 解析url获得objKey
	 * @param path
	 * @param url_type
	 * @return
	 */
	private static String parseObjKey(String path, String url_type){
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains("?")){
			tmp = str.split("?");
			objKey = tmp[0];
		}else{
			objKey = str;
		}
		return objKey;
	}
	
	/**
	 * 对URL进行格式处理
	 * @param path
	 * @return
	 */
	private static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}	
}