package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import org.apache.http.client.methods.HttpGet;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.net.Uri.Builder;

public class FindUserService extends BaseService {
	private String userName;

	public FindUserService(String userName) {
		super(null);
		this.userName = userName;

	}

	private static final String FIND_USER = ServicesConstants.APP_SERVER_URL + "/api/findUser/";

	public JsonElement findUser() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {
		return FIND_USER;
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.USER_NAME, userName);

	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);

	}

}
