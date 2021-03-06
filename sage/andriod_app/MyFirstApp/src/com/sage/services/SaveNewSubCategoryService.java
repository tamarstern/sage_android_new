package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeSubCategory;

import android.app.Activity;
import android.net.Uri.Builder;

public class SaveNewSubCategoryService extends BaseService {

	private static final String DELETE_RECIPE_URL = ServicesConstants.APP_SERVER_URL + "/api/subCategory";

	private RecipeSubCategory category;

	private String token;

	private String username;

	public SaveNewSubCategoryService(RecipeSubCategory category, String token, String username, Activity activity) {
		super(activity);
		this.category = category;
		this.token = token;
		this.username = username;
	}

	public JsonElement saveNewCategory() throws Exception {
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("subCategoryName", category.getHeader()).
		appendQueryParameter("categoryId", category.getCategoryId());
		
		
		
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
