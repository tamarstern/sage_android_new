package com.sage.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sage.entities.RecipeDetails;

public class ServicesUtils {
	
	private static final String RECIPE_TYPE = "recipeType";
	
	public static RecipeDetails createRecipeDetailsFromResponse(Gson gson, JsonObject dataElement) {
		String type = dataElement.get(RECIPE_TYPE).getAsString();

		RecipeDetails recipeDetails = gson.fromJson(dataElement, RecipeDetails.class);

		return recipeDetails;
	}

}
