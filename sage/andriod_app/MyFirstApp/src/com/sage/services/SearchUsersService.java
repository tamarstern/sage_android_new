package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import org.apache.http.client.methods.HttpGet;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.net.Uri.Builder;

public class SearchUsersService extends BaseService {

	private String token;
	private String textToSearch;
	private int pageNumber;
	
	
	public SearchUsersService(String token, String textToSearch, int pageNumber){
		super(null);
		this.token = token;
		this.textToSearch = textToSearch;
		this.pageNumber = pageNumber;
		
	}

	private static final String GET_FOLLOWING = ServicesConstants.APP_SERVER_URL + 
			"/api//users/searchUsers";

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
		conn.setRequestProperty(ServicesConstants.USER_TEXT_TO_SEARCH, textToSearch);
		conn.addRequestProperty(ServicesConstants.PAGE_NUMBER, String.valueOf(pageNumber));

	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);

	}




}
