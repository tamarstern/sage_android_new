package com.sage.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFetchUsersTask extends AsyncTask<String, Void, JsonElement> {
	
	private Activity activity;

	public BaseFetchUsersTask(Activity activity) {
		this.activity = activity;	
	}


	@Override
	protected JsonElement doInBackground(String... token) {

		try {
			String currentToken = token[0];
			String secondParam = token[1];
			int pageNumber = Integer.valueOf(token[2]);
			return createAndExecuteService(currentToken, secondParam, pageNumber);
		} catch (Exception e) {
			ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
			return null;
		}
	}

	protected abstract void performCustomActionsOnException();


	protected abstract JsonElement createAndExecuteService(String currentToken, String secondParam, int pageNumber) throws Exception;	
	@Override
	protected void onPostExecute(JsonElement result) {
		if (result == null) {
			performCustomActionsOnException();
			return;
		}
		performCustomActionsOnPostExecute();
		JsonObject resultJsonObject = result.getAsJsonObject();

		boolean foundResults = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

		if (foundResults) {
			JsonElement dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
			JsonArray resultDataObject = dataElement.getAsJsonArray();

			Gson gson = new GsonBuilder().create();

			List<User> users = gson.fromJson(resultDataObject, new TypeToken<ArrayList<User>>() {
			}.getType());

			

			initializeUi(users);
		}

	}

	protected abstract void performCustomActionsOnPostExecute();


	protected abstract void initializeUi(List<User> users) ;

	
	
}
