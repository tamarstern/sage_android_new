package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import org.apache.http.client.methods.HttpGet;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.net.Uri.Builder;

public class GetSearchMyRecipes extends BaseService {
	private String token;
	private String userName;
	private String recipText;

	public GetSearchMyRecipes(String token, String userName, String recipText) {
		super(null);
		this.token = token;
		this.userName = userName;
		this.recipText = recipText;
	}

	private static final String GET_RECIPIES_FOR_USER = ServicesConstants.APP_SERVER_URL + "/api/searchMyRecipes/";

	public JsonElement getRecipies() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {
		return GET_RECIPIES_FOR_USER;
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, userName);
		conn.setRequestProperty("recipe_text", recipText);

	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);

	}

}
