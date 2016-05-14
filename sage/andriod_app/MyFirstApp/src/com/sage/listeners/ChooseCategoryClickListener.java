package com.sage.listeners;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.sage.activities.ActivityRecipiesInCategoryPage;
import com.sage.application.RecipesToSaveContainer;
import com.sage.application.UserCategoriesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.tasks.AddRecipeToCategoryTask;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.NavigationUtils;

public class ChooseCategoryClickListener implements OnClickListener {

	/**
	 * 
	 */
	private final Activity context;
	RecipeDetails recipeDetails;
	RecipeCategory recipeCategory;

	public ChooseCategoryClickListener(Activity context, RecipeDetails recipeDetails,
			RecipeCategory recipeSubCategory) {
		this.context = context;
		this.recipeDetails = recipeDetails;
		this.recipeCategory = recipeSubCategory;

	}

	private void handleSaveRecipeFailure() {
		UserCategoriesContainer.getInstance().
				updateRecipeForCategoryInCache(recipeDetails, null, recipeCategory.get_id());
		NavigationUtils.openRecipesPerCategoryPage(context, recipeCategory);
	}


	@Override
	public void onClick(View v) {

		if (recipeDetails != null) {
			if(recipeDetails.isExceptionOnSave()) {
				RecipesToSaveContainer.getInstance().updateNewRecipeToSaveWithCategory(recipeDetails, recipeCategory);
				handleSaveRecipeFailure();
			} else {
				attacheRecipeToCategory();
			}
		} else {
			openCategoryPage();
		}

	}


	private void attacheRecipeToCategory() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		Object[] params = new Object[] { recipeCategory, token, userName, recipeDetails };

		new AddToSubCategoryTask().execute(params);

		AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.ADD_RECIPE_TO_SUB_CATEGORY);
	}

	private void openCategoryPage() {
		Intent intent = new Intent(context, ActivityRecipiesInCategoryPage.class)
				.putExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER, recipeCategory);
		context.startActivity(intent);
	}

	private class AddToSubCategoryTask extends AddRecipeToCategoryTask {

		public AddToSubCategoryTask() {
			super(context);
		}

		@Override
		protected void doHandleFailure() {
			recipeDetails.setExceptionOnSave(true);
			RecipesToSaveContainer.getInstance().addAttachCategoryToSave(recipeDetails, recipeCategory);
			handleSaveRecipeFailure();
		}

		@Override
		protected void doHandleSuccess() {
			String oldId = recipeDetails.getCategoryId();
			String newId = recipeCategory.get_id();
			UserCategoriesContainer.getInstance().
					updateRecipeForCategoryInCache(recipeDetails, oldId, newId);
			openCategoryPage();

		}

	}

}