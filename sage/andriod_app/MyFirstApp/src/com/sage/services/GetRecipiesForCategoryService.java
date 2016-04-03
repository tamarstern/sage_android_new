package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeSubCategory;

import android.net.Uri.Builder;

public class GetRecipiesForCategoryService extends BaseService {

	private String token;
	private String userName;
	private RecipeCategory category;
	
	public GetRecipiesForCategoryService(String token, String userName, RecipeCategory subCategory){
		super(null);
		this.token = token;
		this.userName = userName;
		this.category = subCategory;
	}

	private static final String GET_RECIPIES_FOR_USER = ServicesConstants.APP_SERVER_URL + "/api/recipesByCategory/";

	public JsonElement getRecipes() throws Exception {

		return super.service();
	}

	@Override
	protected String getUrl() {
		return GET_RECIPIES_FOR_USER  + category.get_id();
	}

	
	
	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod(ServicesConstants.GET_REQUEST_TYPE);

	}

	@Override
	protected void addBodyParameters(Builder builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, userName);
		
	}




}
