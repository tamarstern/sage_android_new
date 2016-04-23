package com.sage.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.services.RemoveLikeService;
import com.sage.utils.ActivityUtils;

public abstract class RemoveLikeTask extends AsyncTask<Object, Void, JsonElement> {

	private String token;
	private String userName;
	private String recipeId;
	private Activity activity;
	private boolean featuredRecipe;

	public RemoveLikeTask(Activity activity, boolean featuredRecipe) {
		this.activity = activity;
		this.featuredRecipe = featuredRecipe;

	}

	@Override
	protected JsonElement doInBackground(Object... params) {

		try {
			recipeId = (String) params[0];
			token = (String) params[1];
			userName = (String) params[2];
			RemoveLikeService service = new RemoveLikeService(recipeId, token, userName,featuredRecipe, activity);
			return service.removeLike();
		} catch (Exception e) {
			ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(JsonElement result) {
		if (result == null) {
			return;
		}
		JsonObject resultJsonObject = result.getAsJsonObject();
		boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
		if (saveSucceed) {

			handleSuccess( resultJsonObject);
		}

	}

	public abstract void handleSuccess(JsonObject resultJsonObject) ;
}
