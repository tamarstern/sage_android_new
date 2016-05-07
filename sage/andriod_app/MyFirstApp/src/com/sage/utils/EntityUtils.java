package com.sage.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeCategoryBase;
import com.sage.entities.RecipeDetails;

import java.util.UUID;

public class EntityUtils {

	public static boolean isNewRecipe(RecipeDetails recipeDetails) {
		return TextUtils.isEmpty(recipeDetails.get_id());
	}
	

	public static boolean isLoggedInUserRecipe(String userId, Context context) {
		if (TextUtils.isEmpty(userId)) {
			return true;
		}

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

		String loggedInUserName = sharedPref.getString(ActivityConstants.USER_NAME, null);

		if (userId.equals(loggedInUserName)) {
			return true;
		}
		return false;
	}

	public static boolean isRecipeChanges(Context context, RecipeDetails recipeDetails) {
		return recipeDetails != null && ((EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), context)
				&& recipeDetails.isRecipeChanges()) || isNewRecipe(recipeDetails));
	}

	public static boolean isNewCategory(RecipeCategoryBase category) {
		return category == null || TextUtils.isEmpty(category.get_id());
	}

	public static void generateRecipeFakeId(RecipeDetails details) {
		details.set_id(UUID.randomUUID().toString());
	}


}
