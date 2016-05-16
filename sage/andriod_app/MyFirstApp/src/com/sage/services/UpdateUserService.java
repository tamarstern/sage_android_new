package com.sage.services;

import android.app.Activity;
import android.net.Uri.Builder;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class UpdateUserService extends BaseService {

	private static final String REGISTER_NEW_URL = ServicesConstants.APP_SERVER_URL + "/api//users";

	private String username;
	private String password;
	private String userDisplayName;
	private String gcmToken;
	
	public UpdateUserService(String username, String password, String userDisplayName, String gcmToken, Activity activity) {
		super(activity);
		this.password = password;
		this.username = username;
		this.userDisplayName = userDisplayName;
		this.gcmToken = gcmToken;
	}

	public JsonElement updateUser() throws Exception {	
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("username", username);
		if(!TextUtils.isEmpty(password)) {
			builder.appendQueryParameter("password", password);
		}
		if(!TextUtils.isEmpty(userDisplayName)) {
			builder.appendQueryParameter("userDisplayName", userDisplayName);
		}
		if(!TextUtils.isEmpty(gcmToken)) {
			builder.appendQueryParameter("newGcmToken", gcmToken);
		}
		
	}

	@Override
	protected String getUrl() {

		return REGISTER_NEW_URL;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.PUT_REQUEST_TYPE);
		
	}

}
