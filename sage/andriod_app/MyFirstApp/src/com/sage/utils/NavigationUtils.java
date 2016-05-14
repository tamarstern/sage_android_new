package com.sage.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sage.activities.ActivityRecipiesInCategoryPage;
import com.sage.activities.NewsfeedActivity;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;

public class NavigationUtils {
	public static void openNewsfeed(Context context) {
		Intent intent = new Intent(context, NewsfeedActivity.class);
		context.startActivity(intent);
	}

	public static void openRecipesPerCategoryPage(Activity context, RecipeCategory category) {
		Intent intent = new Intent(context, ActivityRecipiesInCategoryPage.class)
				.putExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER, category);
		context.startActivity(intent);
	}
}
