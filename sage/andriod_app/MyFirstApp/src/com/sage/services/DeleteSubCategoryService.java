package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeDetails;

import android.app.Activity;
import android.net.Uri.Builder;

public class DeleteSubCategoryService extends BaseService {
	
	private static final String DELETE_RECIPE_URL = ServicesConstants.APP_SERVER_URL + "/api//subCategory";
	
	private String categoryId;
	
	private String token;
	
	private String username;
	
	public DeleteSubCategoryService(String categoryId, String token, String username, Activity activity) {
		super(activity);
		this.categoryId = categoryId;
		this.token = token;
		this.username = username;
	}
	
	public JsonElement deleteSubCategory() throws Exception {	
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("subCategoryId", categoryId);
		
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
		conn.setRequestMethod(ServicesConstants.DELETE_REQUEST_TYPE);
		
	}

}
