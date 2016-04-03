package com.sage.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeLinkDetails;
import com.sage.entities.RecipePictureDetails;
import com.sage.entities.RecipeTextDetails;
import com.sage.entities.RecipeType;

public class ServicesUtils {
	
	private static final String RECIPE_TYPE = "recipeType";
	
	public static RecipeDetails createRecipeDetailsFromResponse(Gson gson, JsonObject dataElement) {
		String type = dataElement.get(RECIPE_TYPE).getAsString();

		RecipeDetails recipeDetails = null;

		if (type.equals(RecipeType.TEXT.toString())) {
			recipeDetails = gson.fromJson(dataElement, RecipeTextDetails.class);
		} else if (type.equals(RecipeType.PICTURE.toString())) {
			recipeDetails = gson.fromJson(dataElement, RecipePictureDetails.class);
		} else {
			recipeDetails = gson.fromJson(dataElement, RecipeLinkDetails.class);
		}
		return recipeDetails;
	}

}
