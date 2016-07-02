package com.sage.constants;

public class ServicesConstants {
	
	//public static String APP_SERVER_URL = "http://10.0.2.2:1337"
	
	//public static String APP_SERVER_URL = "https://192.168.0.180:1337";
	//public static String APP_SERVER_URL = "https:// 192.168.43.91:1337";

	public static String APP_SERVER_URL = "https://sageproductionservice.azurewebsites.net/";
	public static final String UTF_8 = "UTF-8";
	public static final String POST_REQUEST_TYPE = "POST";
	public static final String PUT_REQUEST_TYPE = "PUT";
	public static final String X_ACCESS_TOKEN = "x-access-token";
	public static final int READ_TIMEOUT = 10000;
	public static final int CONNECTION_TIMEOUT = 15000;
	public static final String USER_NAME = "username";
	public static final String USER_TEXT_TO_SEARCH = "user_text";
	public static final String GET_REQUEST_TYPE = "GET";
	public static final String DELETE_REQUEST_TYPE = "DELETE";
	public static final String PAGE_NUMBER = "pagenumber";

	public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
	public static final String REGISTRATION_COMPLETE = "registrationComplete";

	public static final String PICTURE_URL_POST = APP_SERVER_URL + "/api/recipesPerUser/picture/{0}?imageType={1}&token={2}&userObjectId={3}";

	public static final String PICTURE_URL_GET = APP_SERVER_URL + "/api/recipesPerUser/picture/{0}";



}
