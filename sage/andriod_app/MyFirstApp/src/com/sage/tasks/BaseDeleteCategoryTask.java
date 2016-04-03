package com.sage.tasks;

import java.io.IOException;

import com.example.myfirstapp.ProgressDialogContainer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.utils.ActivityUtils;

import android.app.Activity;
import android.os.AsyncTask;

public abstract class BaseDeleteCategoryTask extends AsyncTask<Object, Void, JsonElement> {

	private ProgressDialogContainer container;

	protected Activity context;

	public BaseDeleteCategoryTask(Activity context) {
		container = new ProgressDialogContainer(context);
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		container.showProgress();
	}

	@Override
	protected JsonElement doInBackground(Object... params) {

		try {
			String categoryId = (String) params[0];
			String token = (String) params[1];
			String userName = (String) params[2];

			return createAndExecuteService(categoryId, token, userName);
		} catch (Exception e) {
			container.dismissProgress();
			ActivityUtils.HandleConnectionUnsuccessfullToServer(context);
			return null;
		}
	}

	protected abstract JsonElement createAndExecuteService(String categoryId, String token, String userName)
			throws Exception;

	@Override
	protected void onPostExecute(JsonElement result) {
		container.dismissProgress();
		if (result == null) {
			return;
		}
		JsonObject resultJsonObject = result.getAsJsonObject();
		boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
		if (saveSucceed) {

			JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

			handleSuccess(dataElement);
		} else {
			handleFailure();

		}

	}

	protected abstract void handleFailure();

	protected abstract void handleSuccess(JsonObject dataElement);

}
