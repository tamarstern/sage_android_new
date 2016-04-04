package com.sage.utils;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeTextDetails;

public class RecipeDetailsBinder {

	public static void bindTextBoxToTitle(EditText title, final RecipeDetails deatils, final Activity activity) {

		title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String recipeTitle = s.toString();
				if(!TextUtils.isEmpty(recipeTitle)) {
					deatils.setHeader(recipeTitle);
					deatils.setRecipeChanges(true);
					AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.CLICK_ADD_PICTURE_RECIPE);

				}

			}
		});
	}

	public static void bindTextBoxToComments(EditText title, final RecipeDetails deatils) {

		title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				deatils.setPreparationComments(s.toString());
				deatils.setRecipeChanges(true);

			}
		});
	}

	public static void bindTextBoxToPreparation(EditText title, final RecipeTextDetails deatils) {

		title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				deatils.setPreparationDescription(s.toString());
				deatils.setRecipeChanges(true);

			}
		});
	}

	public static void bindTextBoxToIngredients(EditText title, final RecipeTextDetails deatils) {

		title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				deatils.setIngredients(s.toString());
				deatils.setRecipeChanges(true);

			}
		});
	}

}
