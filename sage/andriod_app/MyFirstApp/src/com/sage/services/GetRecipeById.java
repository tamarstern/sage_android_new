package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.net.Uri.Builder;

public class GetRecipeById extends BaseService {

	private String token;
	private String userName;
	private String recipeId;
	
	public GetRecipeById(String token, String userName, String recipeId){
		super(null);
		this.token = token;
		this.userName = userName;
		this.recipeId = recipeId;
	}

	private static final String GET_RECIPIES_FOR_USER = ServicesConstants.APP_SERVER_URL+ "/api/recipesPerUser/";

	public JsonElement getRecipe() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {
		return GET_RECIPIES_FOR_USER  + recipeId;
	}

	
	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);
		
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, userName);
		
	}




}
