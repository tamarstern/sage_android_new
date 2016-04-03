package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeDetails;

import android.app.Activity;
import android.net.Uri.Builder;

public class DeleteRecipeService extends BaseService {
	
	private static final String DELETE_RECIPE_URL = ServicesConstants.APP_SERVER_URL + "/api//recipesPerUser";
	
	private RecipeDetails recipeDetails;
	
	private String token;
	
	private String username;
	
	public DeleteRecipeService(RecipeDetails recipeDetails, String token, String username, Activity activity) {
		super(activity);
		this.recipeDetails = recipeDetails;
		this.token = token;
		this.username = username;
	}
	
	public JsonElement deleteRecipe() throws Exception {	
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	protected String getUrl() {
		return DELETE_RECIPE_URL + "/" + recipeDetails.get_id();
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, username);
		
	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.DELETE_REQUEST_TYPE);
		
	}

}
