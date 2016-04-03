package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.net.Uri.Builder;

public class GetCategoriesForUserService extends BaseService {
	private String token;
	private String userName;
	
	public GetCategoriesForUserService(String token, String userName){
		super(null);
		this.token = token;
		this.userName = userName;
	}

	private static final String GET_RECIPIES_FOR_USER = ServicesConstants.APP_SERVER_URL + "/api/category/";

	public JsonElement getCategories() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {
		return GET_RECIPIES_FOR_USER;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.addRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.addRequestProperty(ServicesConstants.USER_NAME, userName);
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
