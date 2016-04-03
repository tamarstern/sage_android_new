package com.sage.tasks;

import java.io.IOException;

import com.example.myfirstapp.ProgressDialogContainer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.CopyExistingRecipeService;
import com.sage.utils.ActivityUtils;
import com.sage.utils.ServicesUtils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class CopyRecipeTask extends AsyncTask<Object, Void, JsonElement> {	
	
	private String token;
	private String userName;
	private String recipeId;
	private Activity context;
	protected ProgressDialogContainer container;
	
	
	public CopyRecipeTask (Activity context) {
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
			recipeId = (String) params[0];
			token = (String) params[1];
			userName = (String) params[2];				
			CopyExistingRecipeService service = new CopyExistingRecipeService(recipeId, token, userName, context);
			return service.copyRecipe();

		} catch (Exception e) {
			container.dismissProgress();
			Log.d(Context.CONNECTIVITY_SERVICE, "Unable to login. URL may be invalid.");
			return null;
		}
	}

	@Override
	protected void onPostExecute(JsonElement result) {
		container.dismissProgress();
		if(result == null) {
			return;
		}
		JsonObject resultJsonObject = result.getAsJsonObject();
		boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
		if (saveSucceed) {

			Gson gson = new Gson();

			JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

			RecipeDetails recipeDetails = ServicesUtils.createRecipeDetailsFromResponse(gson, dataElement);

			ActivityUtils.openCategoriesPage(recipeDetails, context);

		}

	}


}
