package com.sage.services;

import android.net.Uri.Builder;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class GetLikesService extends BaseService {

	private String token;
	private String recipeId;
	private int pageNumber;

	public GetLikesService(String token, String recipeId, int pageNumber) {
		super(null);
		this.token = token;
		this.recipeId = recipeId;
		this.pageNumber = pageNumber;

	}

	private static final String GET_FOLLOWING = ServicesConstants.APP_SERVER_URL + "/api//recipesPerUser/likes/";

	public JsonElement getUsers() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {

		return GET_FOLLOWING + recipeId;
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
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);

	}

}
