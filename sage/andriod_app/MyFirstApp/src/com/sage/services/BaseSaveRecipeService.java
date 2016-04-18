package com.sage.services;

import android.app.Activity;
import android.net.Uri.Builder;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;

import java.net.HttpURLConnection;

public abstract class BaseSaveRecipeService extends BaseService {

	public BaseSaveRecipeService(Activity activity) {
		super(activity);		
	}

	private RecipeDetails recipeDetails;
	private String token;
	private String userName;

	public JsonElement saveRecipe(RecipeDetails recipeDetails, String token, String userName) throws Exception {
		this.recipeDetails = recipeDetails;
		this.token = token;
		this.userName = userName;
		return super.service();
	}

	private void AddTextRecipeParameters(Builder builder, RecipeDetails recipeDetails) {
		String ingredients = recipeDetails.getIngredients();
		if (!TextUtils.isEmpty(ingredients)) {
			builder.appendQueryParameter("ingredients", ingredients);
		}
		String preparationDescription = recipeDetails.getPreparationDescription();
		if (!TextUtils.isEmpty(preparationDescription)) {
			builder.appendQueryParameter("preparationDescription", preparationDescription);
		}

	}

	@Override
	protected void addBodyParameters(Builder builder) {
		addGenericParameters(builder);

		initRecipeType(recipeDetails, builder);

		if (recipeDetails.getRecipeType().equals(RecipeType.TEXT)) {

			AddTextRecipeParameters(builder, recipeDetails);
		}

		if (recipeDetails.getRecipeType().equals(RecipeType.LINK)) {
			builder.appendQueryParameter("url", recipeDetails.getUrl());
		}

	}

	private void initRecipeType(RecipeDetails recipeDetails, Builder builder) {
		RecipeType type = recipeDetails.getRecipeType();
		switch (type) {
		case TEXT:
			builder.appendQueryParameter("recipeType", RecipeType.TEXT.toString());
			break;
		case LINK:
			builder.appendQueryParameter("recipeType", RecipeType.LINK.toString());
			break;
		default:
			builder.appendQueryParameter("recipeType", RecipeType.PICTURE.toString());
			break;

		}

	}

	private void addGenericParameters(Builder builder) {
		builder.appendQueryParameter("header", recipeDetails.getHeader());
		String preparationComments = recipeDetails.getPreparationComments();
		if(!TextUtils.isEmpty(preparationComments)) {
			builder.appendQueryParameter("preparationComments", preparationComments);
		}		
		builder.appendQueryParameter("published", Boolean.toString(recipeDetails.isPublished()));
	}

	@Override
	protected void addHeaderProperties(HttpURLConnection conn) {
		conn.setRequestProperty(ServicesConstants.X_ACCESS_TOKEN, token);
		conn.setRequestProperty(ServicesConstants.USER_NAME, userName);

	}

}
