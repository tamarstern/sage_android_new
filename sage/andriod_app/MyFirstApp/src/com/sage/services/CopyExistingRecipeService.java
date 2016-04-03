package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeLinkDetails;
import com.sage.entities.RecipeTextDetails;
import com.sage.entities.RecipeType;

import android.app.Activity;
import android.net.Uri.Builder;

public class CopyExistingRecipeService extends BaseService {

	private static final String REGISTER_NEW_URL = ServicesConstants.APP_SERVER_URL + "/api//recipesPerUser/copyRecipe";
	
	private String recipeId;

	private String token;

	private String username;

	public CopyExistingRecipeService(String recipeId, String token, String username, Activity activity) {
		super(activity);
		this.recipeId = recipeId;
		this.token = token;
		this.username = username;
	}

	public JsonElement copyRecipe() throws Exception {
		return super.service();
	}


	@Override
	protected String getUrl() {

		return REGISTER_NEW_URL + "/" + recipeId;
	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.POST_REQUEST_TYPE);

	}

	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, username);
		
	}

}
