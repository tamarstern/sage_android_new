package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.app.Activity;
import android.net.Uri.Builder;
import android.util.Base64;

public class LoginService extends BaseService {

	public LoginService(Activity activity) {
		super(activity);		
	}

	private static final String LOGIN_URL = ServicesConstants.APP_SERVER_URL + "/api//login";

	private String userName;

	private String password;

	public JsonElement login(String username, String password) throws Exception {
		this.userName = username;
		this.password = password;
		return super.service();
	}

	@Override
	protected String getUrl() {

		return LOGIN_URL;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		String userpass = userName + ":" + password;
		String basicAuth = "Basic " + new String(Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT));

		conn.setRequestProperty("Authorization", basicAuth);

	}

	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.POST_REQUEST_TYPE);
	}

}
