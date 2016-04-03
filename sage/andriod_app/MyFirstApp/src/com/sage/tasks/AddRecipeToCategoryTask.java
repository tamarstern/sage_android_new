package com.sage.tasks;

import com.example.myfirstapp.ProgressDialogContainer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.services.AddRecipeToCategoryService;
import com.sage.utils.ActivityUtils;

import android.app.Activity;
import android.os.AsyncTask;

public abstract class AddRecipeToCategoryTask extends AsyncTask<Object, Void, JsonElement> {

	private ProgressDialogContainer container;
	private Activity context;

	public AddRecipeToCategoryTask(Activity context) {
		this.context = context;
		container = new ProgressDialogContainer(context);
	}

	@Override
	protected void onPreExecute() {
		container.showProgress();
	}

	@Override
	protected JsonElement doInBackground(Object... params) {

		try {
			RecipeCategory categoryToSave = (RecipeCategory) params[0];
			String token = (String) params[1];
			String userName = (String) params[2];
			RecipeDetails recipeDetails = (RecipeDetails) params[3];

			AddRecipeToCategoryService service = new AddRecipeToCategoryService(recipeDetails,
					categoryToSave, token, userName, context);
			return service.addRecipeToSubCategory();
		} catch (Exception e) {
			container.dismissProgress();
			ActivityUtils.HandleConnectionUnsuccessfullToServer(context);
			return null;
		}
	}

	@Override
	protected void onPostExecute(JsonElement result) {
		container.dismissProgress();
		if (result == null) {
			return;
		}
		JsonObject resultJsonObject = result.getAsJsonObject();
		boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
		if (saveSucceed) {
			doHandleSuccess();

		}

	}

	protected abstract void doHandleSuccess();

}
