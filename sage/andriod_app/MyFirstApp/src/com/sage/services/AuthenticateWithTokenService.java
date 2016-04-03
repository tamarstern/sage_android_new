package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.app.Activity;
import android.net.Uri.Builder;

public class AuthenticateWithTokenService extends BaseService {

	public AuthenticateWithTokenService(Activity activity) {
		super(activity);		
	}

	private String token;

	private static final String LOGIN_WItH_TOKEN_URL = ServicesConstants.APP_SERVER_URL + "/api//loginWithToken";

	public JsonElement authenticateWithToke(String token) throws Exception {
		
		this.token = token;
		return super.service();
	}

	@Override
	protected String getUrl() {
		return LOGIN_WItH_TOKEN_URL;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);

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
