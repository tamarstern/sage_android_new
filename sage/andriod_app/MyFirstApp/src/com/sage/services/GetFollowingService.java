package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.net.Uri.Builder;

public class GetFollowingService extends BaseService {

	private String token;
	private String userName;
	private int pageNumber;

	public GetFollowingService(String token, String userName, int pageNumber) {
		super(null);
		this.token = token;
		this.userName = userName;
		this.pageNumber = pageNumber;

	}

	private static final String GET_FOLLOWING = ServicesConstants.APP_SERVER_URL + "/api//profile/getFollowers";

	public JsonElement getUsers() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {
		return GET_FOLLOWING;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, userName);
		conn.addRequestProperty(ServicesConstants.PAGE_NUMBER, String.valueOf(pageNumber));
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
