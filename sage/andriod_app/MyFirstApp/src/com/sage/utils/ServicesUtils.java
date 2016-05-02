package com.sage.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sage.constants.ImageType;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.tasks.SetRecipeImageTask;

import java.io.File;

public class ServicesUtils {
	
	private static final String RECIPE_TYPE = "recipeType";
	
	public static RecipeDetails createRecipeDetailsFromResponse(Gson gson, JsonObject dataElement) {
		String type = dataElement.get(RECIPE_TYPE).getAsString();

		RecipeDetails recipeDetails = gson.fromJson(dataElement, RecipeDetails.class);

		return recipeDetails;
	}

	public static void saveRecipeImage(RecipeDetails recipe, String token, Context context) {
		Bitmap recipeMainImage = recipe.getImage();
		if (recipeMainImage != null) {
			Object[] mainImageParams = new Object[]{ token,
					context.getFilesDir().getPath().toString() + File.separator + recipe.get_id()};
			new SetRecipeImageTask(ImageType.RECIPE_PICTURE, recipe, recipeMainImage).execute(mainImageParams);
		}
		if (!(recipe.getRecipeType().equals(RecipeType.PICTURE))) {
			return;
		}
		Bitmap recipeAsPictureImage = (recipe).getRecipeAsPictureImage();
		if (recipeAsPictureImage != null) {
			Object[] recipePictureParams = new Object[]{token,
					context.getFilesDir().getPath().toString() + File.separator + recipe.get_id()};
			new SetRecipeImageTask(ImageType.IMAGE_RECIPE_PICTURE, recipe, recipeAsPictureImage)
					.execute(recipePictureParams);
		}
	}


}
