package com.campusrecruit.bean;

import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.campusrecruit.common.StringUtils;

public class URLs implements Serializable {
	
	public final static String HOST = "lib.youionline.com:9080";
//	public final static String HOST = "10.108.208.64:8080";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	private final static String URL_SPLITTER = "/";
	private final static String URL_MOBILE = "mobile/";
	private final static String URL_UNDERLINE = "_";
	private final static String JSON = ".json";
	
	
	
	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER + URL_MOBILE;
	
	
	public final static String NOTICE_GET = URL_API_HOST+"notice" + JSON;
	public final static String NOTICE_CLEAR = URL_API_HOST +"noticeClear" + JSON;
	
	public final static String USER_INFO = URL_API_HOST+"user/get" + JSON;
	public final static String USER_LOGIN = URL_API_HOST+"user/login" + JSON;
	public final static String USER_LOGOUT = URL_API_HOST+"user/logout" + JSON;
	public final static String USER_REGISTER = URL_API_HOST+"user/register" + JSON;
	public final static String USER_UPDATE_PORTRAIT = URL_API_HOST + "user/portrait/update" + JSON;
	public final static String BBSREPLY_BY_OTHER_LIST = URL_API_HOST+"user/replies" + JSON;
	public final static String BBSREPLY_TO_OTHER_LIST = URL_API_HOST+"user/comments" + JSON;
	public final static String USER_FACE = URL_API_HOST + "user/portrait/download" + JSON;
	
	public final static String USER_TOPIC_LIST = URL_API_HOST+"user/topics" + JSON;
	public final static String USER_CONTACT_LIST = URL_API_HOST+"user/message/contacts" + JSON;
	public final static String USER_MESSAGE_LIST = URL_API_HOST+"user/message/list" + JSON;
	public final static String USER_MESSAGE_NEW = URL_API_HOST+"user/message/new" + JSON;
	
	
	public final static String COMPANY_DETAIL = URL_API_HOST+"company" + JSON;
	
	public final static String USER_SET_INFO = URL_API_HOST+"user/set" + JSON;
	public final static String USER_SET_PREFERENCE = URL_API_HOST+"user/preference/set" + JSON;
	
	public final static String USER_SET_NOTIFY = URL_API_HOST+"preference/setNotify" + JSON;
	
	public final static String JOIN_CAREERTALK = URL_API_HOST+"careerTalk/join" + JSON;
	public final static String JOIN_RECRUIT = URL_API_HOST+"recruit/join" + JSON;
	public final static String JOIN_BBSSECTION = URL_API_HOST+"bbsSection/join" + JSON;
	
	public final static String CAREERTALK_LIST = URL_API_HOST+"careerTalk/list" + JSON;
	public final static String CAREERTALK_CLICK = URL_API_HOST+"careerTalk/click" + JSON;
	public final static String CAREERTALK_DETAIL = URL_API_HOST+"careerTalk/detail" + JSON;
	public final static String CAREERTALK_SEARCH = URL_API_HOST+"careerTalk/search" + JSON;
	
	public final static String RECRUIT_LIST = URL_API_HOST+"recruit/list" + JSON;
	public final static String RECRUIT_DETAIL = URL_API_HOST+"recruit/detail" + JSON;
	public final static String RECRUIT_JOB_DESCRIPTION = URL_API_HOST+"recruit/description" + JSON;
	public final static String RECRUIT_JOB_PROCESS_INFO = URL_API_HOST+"recruit/contact" + JSON;
	public final static String RECRUIT_SEARCH = URL_API_HOST+"recruit/search" + JSON;
	
	public final static String BBSSECTION_LIST = URL_API_HOST+"bbsSection/list" + JSON;
	public final static String BBSSECTION_SEARCH = URL_API_HOST+"bbsSection/search" + JSON;
	public final static String BBSTOPIC_LIST = URL_API_HOST+"bbsTopic/list" + JSON;
	public final static String BBSTOPIC_NEW = URL_API_HOST+"new/bbsTopic" + JSON;
	public final static String BBSTOPIC_DETAIL = URL_API_HOST + "bbsTopic/detail" + JSON;
	public final static String BBSREPLY_LIST = URL_API_HOST+"bbsReply/list" + JSON;
	public final static String BBSREPLY_NEW = URL_API_HOST+"new/bbsReply" + JSON ;
	public final static String BBSREPLY_DEL = URL_API_HOST+"del/bbsReply" + JSON;
	
	public final static String UPDATE_VERSION = URL_API_HOST+"AppVersion.json";
	
	
	
	public final static int URL_OBJ_TYPE_OTHER = 0x000;
	public final static int URL_OBJ_TYPE_NEWS = 0x001;
	public final static int URL_OBJ_TYPE_SOFTWARE = 0x002;
	public final static int URL_OBJ_TYPE_QUESTION = 0x003;
	public final static int URL_OBJ_TYPE_ZONE = 0x004;
	public final static int URL_OBJ_TYPE_BLOG = 0x005;
	public final static int URL_OBJ_TYPE_TWEET = 0x006;
	public final static int URL_OBJ_TYPE_QUESTION_TAG = 0x007;
	
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
//	public final static URLs parseURL(String path) {
//		if(StringUtils.isEmpty(path))return null;
//		path = formatURL(path);
//		URLs urls = null;
//		String objId = "";
//		try {
//			URL url = new URL(path);
//			//站内链接
//			if(url.getHost().contains(URL_HOST)){
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
//			e.printStackTrace();
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
	private final static String parseObjId(String path, String url_type){
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
	private final static String parseObjKey(String path, String url_type){
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
	private final static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}	
}