package com.sage.listeners;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.sage.entities.RecipeDetails;
import com.sage.utils.ActivityUtils;

public class RecipeDetailsClickListener implements OnClickListener {

	/**
	 * 
	 */
	private final Activity context;
	private RecipeDetails recipeId;

	public RecipeDetailsClickListener(Activity context, RecipeDetails recipeId) {
		this.context = context;
		this.recipeId = recipeId;
	}

	@Override
	public void onClick(View v) {

		ActivityUtils.openRecipeActivity(recipeId, context);
	}


}