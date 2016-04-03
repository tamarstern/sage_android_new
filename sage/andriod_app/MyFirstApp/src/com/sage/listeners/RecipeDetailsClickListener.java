package com.sage.listeners;

import java.io.IOException;

import com.example.myfirstapp.LinkRecipePageActivity;
import com.example.myfirstapp.PictureRecipePageActivity;
import com.example.myfirstapp.ProgressDialogContainer;
import com.example.myfirstapp.TextReciptPageActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.constants.ServicesConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeBasicData;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeLinkDetails;
import com.sage.entities.RecipePictureDetails;
import com.sage.entities.RecipeTextDetails;
import com.sage.entities.RecipeType;
import com.sage.services.GetRecipeById;
import com.sage.utils.ActivityUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ServicesUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class RecipeDetailsClickListener implements OnClickListener {

	/**
	 * 
	 */
	private final Activity context;
	private String recipeId;

	public RecipeDetailsClickListener(Activity context, String recipeId) {
		this.context = context;
		this.recipeId = recipeId;
	}

	@Override
	public void onClick(View v) {

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		String[] params = new String[] { token, userName, recipeId };

		new GetRecipeByIdTask(context).execute(params);

	}
	


	private class GetRecipeByIdTask extends AsyncTask<String, Void, JsonElement> {
		private Activity context;
		private ProgressDialogContainer container;

		GetRecipeByIdTask(Activity context) {
			this.context = context;
			container = new ProgressDialogContainer(context);
		}
		
		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement doInBackground(String... token) {

			try {
				String currentToken = token[0];
				String userName = token[1];
				String recipeId = token[2];

				GetRecipeById service = new GetRecipeById(currentToken, userName, recipeId);

				return service.getRecipe();
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

			boolean recipiesFound = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

			if (recipiesFound) {
				JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

				Gson gson = new GsonBuilder().create();

				RecipeDetails details = ServicesUtils.createRecipeDetailsFromResponse(gson, dataElement);

				ActivityUtils.openRecipeActivity(details, context);
			}

		}

	
	}

}