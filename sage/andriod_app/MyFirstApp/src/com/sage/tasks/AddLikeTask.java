package com.sage.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.services.SaveNewLikeService;
import com.sage.utils.ActivityUtils;

public abstract class AddLikeTask extends AsyncTask<Object, Void, JsonElement> {

	private String token;
	private String userName;
	private String recipeId;
	private Activity activity;
	private boolean featuredRecipe;

	public AddLikeTask(Activity activity, boolean featuredRecipe) {
		this.activity = activity;
		this.featuredRecipe = featuredRecipe;

	}

	@Override
	protected JsonElement doInBackground(Object... params) {

		try {
			recipeId = (String) params[0];
			token = (String) params[1];
			userName = (String) params[2];		
			SaveNewLikeService service = new SaveNewLikeService(recipeId, token, userName,featuredRecipe, activity);
			return service.saveLike();
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
			handleSuccess(resultJsonObject);
		}

	}

	protected abstract void handleSuccess(JsonObject resultJsonObject);

}
