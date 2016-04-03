package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.app.Activity;
import android.net.Uri.Builder;

public class UnfollowUserService extends BaseService {

	private static final String REGISTER_NEW_URL = ServicesConstants.APP_SERVER_URL + "/api//profile/unfollow";

	private String username;
	private String userNameToUnFollow;
	private String token;



	public UnfollowUserService(String token, String username, String userNameToUnFollow, Activity activity) {
		super(activity);
		this.username = username;
		this.token = token;
		this.userNameToUnFollow = userNameToUnFollow;
	}

	
	public JsonElement unfollowUser() throws Exception {
	
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("username", userNameToUnFollow);

	}

	@Override
	protected String getUrl() {

		return REGISTER_NEW_URL;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, username);


	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.POST_REQUEST_TYPE);
		
	}

}
