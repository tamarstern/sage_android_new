package com.sage.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.services.GetRecipeUrlDetailsService;
import com.sage.utils.ActivityUtils;

public abstract class GetRecipeUrlDetailsTask extends AsyncTask<Object, Void, JsonElement> {

	private String token;
	private String userName;
	private String url;
	
	private Activity context;
	
	public GetRecipeUrlDetailsTask(Activity context) {
		this.context = context;	

	}

	@Override
	protected JsonElement doInBackground(Object... params) {

		try {
			url = (String) params[0];
			token = (String) params[1];
			userName = (String) params[2];
			GetRecipeUrlDetailsService service = new GetRecipeUrlDetailsService(url, token, userName);
			return service.getUrlDetails();
		} catch (Exception e) {
			ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(JsonElement result) {
		if (result == null) {
			handleFailure();
			return;
		}
		JsonObject resultJsonObject = result.getAsJsonObject();
		boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
		if (saveSucceed) {
			handleSuccess(resultJsonObject);
		} else {
			handleFailure();
		}

	}

	protected abstract void handleSuccess(JsonObject resultJsonObject);


	protected void handleFailure() {

	}

}
