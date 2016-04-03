package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeSubCategory;

import android.app.Activity;
import android.net.Uri.Builder;

public class AddRecipeToCategoryService extends BaseService {
	
	private static final String DELETE_RECIPE_URL = ServicesConstants.APP_SERVER_URL + "/api/relateRecipeToCategory";

	private RecipeCategory category;

	private RecipeDetails recipeDetails;

	private String token;

	private String username;

	public AddRecipeToCategoryService(RecipeDetails recipeDetails, RecipeCategory category, 
			String token,String username, Activity activity) {
		super(activity);
		this.category = category;
		this.token = token;
		this.username = username;
		this.recipeDetails = recipeDetails;
	}

	public JsonElement addRecipeToSubCategory() throws Exception {
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("categoryId", category.get_id()).
		appendQueryParameter("recipeId",recipeDetails.get_id());

	}

	@Override
	protected String getUrl() {
		return DELETE_RECIPE_URL;
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, username);

	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.POST_REQUEST_TYPE);

	}

}
