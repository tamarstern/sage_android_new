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

		RecipeDetails recipeDetails = gson.fromJson(dataElement, RecipeDetails.class);

		return recipeDetails;
	}

	public static void saveRecipeMainPicture(String recipeId, Bitmap recipeMainImage, Context context, String token) {
		if (recipeMainImage != null) {
			Object[] mainImageParams = new Object[]{ token,
					context.getFilesDir().getPath().toString() + File.separator + recipeId};
			new SetRecipeImageTask(ImageType.RECIPE_PICTURE, recipeId, recipeMainImage).execute(mainImageParams);
		}
	}

	public static void saveRecipeImagePicture(String recipeId, Bitmap recipeAsPictureImage, Context context, String token) {
		if (recipeAsPictureImage != null) {
			Object[] recipePictureParams = new Object[]{token,
					context.getFilesDir().getPath().toString() + File.separator + recipeId};
			new SetRecipeImageTask(ImageType.IMAGE_RECIPE_PICTURE, recipeId, recipeAsPictureImage)
					.execute(recipePictureParams);
		}
	}



	public static void saveRecipeImage(RecipeDetails recipe, String token, Context context, String userObjectId) {
		Bitmap recipeMainImage = recipe.getImage();
		saveRecipeMainPicture(recipe.get_id(), recipeMainImage, context, token);
		if (!(recipe.getRecipeType().equals(RecipeType.PICTURE))) {
			return;
		}
		Bitmap recipeAsPictureImage = (recipe).getRecipeAsPictureImage();
		saveRecipeImagePicture(recipe.get_id(), recipeAsPictureImage, context, token);
	}


}
