package com.sage.services;

import android.net.Uri.Builder;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class SearchFollowedByService extends BaseService {

	private String token;
	private String userName;
	private String textToSearch;
	private int pageNumber;


	public SearchFollowedByService(String token, String userName, String textToSearch, int pageNumber) {
		super(null);
		this.token = token;
		this.userName = userName;
		this.textToSearch = textToSearch;
		this.pageNumber = pageNumber;
	}

	private static final String GET_FOLLOWING = ServicesConstants.APP_SERVER_URL + "/api//profile/searchMyFollowedBy";

	public JsonElement getUsers() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {
		return GET_FOLLOWING;
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, userName);
		conn.setRequestProperty(ServicesConstants.USER_TEXT_TO_SEARCH, textToSearch);
		conn.addRequestProperty(ServicesConstants.PAGE_NUMBER, String.valueOf(pageNumber));

	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);

	}

}