package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.app.Activity;
import android.net.Uri.Builder;

public class RegisterNewService extends BaseService {

	public RegisterNewService(Activity activity) {
		super(activity);		
	}

	private static final String REGISTER_NEW_URL = ServicesConstants.APP_SERVER_URL + "/api//users";

	private String userDisplayName;
	private String username;
	private String password;

	public JsonElement registerNewUser(String userDisplayName, String username, String password) throws Exception {
		this.userDisplayName = userDisplayName;
		this.password = password;
		this.username = username;
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("username", username).appendQueryParameter("password", password)
				.appendQueryParameter("userDisplayName", userDisplayName);

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
		conn.setRequestMethod(ServicesConstants.POST_REQUEST_TYPE);
		
	}

}
