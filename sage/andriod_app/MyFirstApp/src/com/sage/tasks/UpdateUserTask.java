package com.sage.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.myfirstapp.ProgressDialogContainer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.services.UpdateUserService;
import com.sage.utils.ActivityUtils;

public abstract class UpdateUserTask extends AsyncTask<String, Void, JsonElement> {

	private Activity activity;

	private String userName;

	private String password;

	private String userDisplayName;

	private ProgressDialogContainer container;

	public UpdateUserTask(Activity activity) {
		this.activity = activity;
		container = new ProgressDialogContainer(activity);
	}

	@Override
	protected void onPreExecute() {

		container.showProgress();
	}

	@Override
	protected JsonElement doInBackground(String... params) {

		try {
			userName = (String) params[0];
			password = (String) params[1];
			userDisplayName = (String) params[2];

			UpdateUserService service = new UpdateUserService(userName, password, userDisplayName, activity);
			return service.updateUser();
		} catch (Exception e) {
			ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
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
		boolean updateSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
		if (updateSuccess) {
			handleSuccess();
		}

	}

	public abstract void handleSuccess();

}
