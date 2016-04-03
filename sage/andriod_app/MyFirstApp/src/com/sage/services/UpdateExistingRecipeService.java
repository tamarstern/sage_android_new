package com.sage.services;

import java.net.HttpURLConnection;
import java.net.ProtocolException;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeDetails;

import android.app.Activity;

public class UpdateExistingRecipeService extends BaseSaveRecipeService {

	public UpdateExistingRecipeService(Activity activity) {
		super(activity);	
	}

	private static final String REGISTER_NEW_URL = ServicesConstants.APP_SERVER_URL + "/api//recipesPerUser";
	
	private RecipeDetails recipeDetails;

	public JsonElement saveRecipe(RecipeDetails recipeDetails, String token, String userName) throws Exception {

		this.recipeDetails = recipeDetails;
		
		return super.saveRecipe(recipeDetails, token, userName);
	}

	@Override
	protected String getUrl() {

		return REGISTER_NEW_URL + "/" + recipeDetails.get_id();
	}

	@Override
	protected void setHttpMethod(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod("PUT");

	}

}
