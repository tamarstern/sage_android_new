package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.net.Uri.Builder;

public class GetMyProfile extends BaseService {

	private static final String GET_MY_PROFILE = ServicesConstants.APP_SERVER_URL + "/api/profile/getMyProfile/";
	private String userName;
	private String token;
	
	public GetMyProfile(String token, String userName){
		super(null);
		this.userName = userName;
		this.token = token;
	}
	
	@Override
	protected String getUrl() {
		return GET_MY_PROFILE;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, this.userName);
	}

	public JsonElement getMyProfile() throws Exception {
		return super.service();
	}
	
	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);
		
	}

}
