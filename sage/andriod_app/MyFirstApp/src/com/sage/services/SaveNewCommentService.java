package com.sage.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;

import android.app.Activity;
import android.net.Uri.Builder;

public class SaveNewCommentService extends BaseService {
	
	private static final String SAVE_NEW_COMMENT_URL = ServicesConstants.APP_SERVER_URL + "/api/recipesPerUser/comments/";
	
	private String recipeId;
	
	private String text;
	
	private String token;
	
	private String username;
	
	public SaveNewCommentService(String token, String username, String recipeId, String text, Activity activity) {
		super(activity);
		this.token = token;
		this.username = username;
		this.recipeId = recipeId;
		this.text = text;
	}
	
	public JsonElement saveComment() throws Exception {	
		return super.service();
	}

	@Override
	protected void addBodyParameters(Builder builder) {
		builder.appendQueryParameter("text", text);
		
	}

	
	@Override
	protected String getUrl() {
		return SAVE_NEW_COMMENT_URL  + recipeId;
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
