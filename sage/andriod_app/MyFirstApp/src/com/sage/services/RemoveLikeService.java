package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.app.Activity;
import android.net.Uri.Builder;

public class RemoveLikeService extends BaseService {

	private static final String SAVE_NEW_COMMENT_URL = ServicesConstants.APP_SERVER_URL + "/api/recipesPerUser/unlike/";

	private String recipeId;

	private String token;

	private String username;

	private boolean featuredRecipe;
	
	public RemoveLikeService(String recipeId, String token, String username,boolean featuredRecipe, Activity activity) {
		super(activity);
		this.token = token;
		this.username = username;
		this.recipeId = recipeId;
		this.featuredRecipe = featuredRecipe;
	}

	public JsonElement removeLike() throws Exception {
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("featured_recipe", Boolean.toString(featuredRecipe));
	}

	@Override
	protected String getUrl() {
		return SAVE_NEW_COMMENT_URL + recipeId;
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
