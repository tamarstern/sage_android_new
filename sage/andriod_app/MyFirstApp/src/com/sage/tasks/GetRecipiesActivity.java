package com.sage.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.myfirstapp.ProgressDialogContainer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.utils.ActivityUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class GetRecipiesActivity<T extends RecipeDetails> extends AsyncTask<Object, Void, JsonElement> {

	protected ArrayList<T> details;

	private Type type;

	private Activity context;
	protected ProgressDialogContainer container;

	public GetRecipiesActivity(Type type, Activity _context) {
		this.type = type;
		this.context = _context;
		container = new ProgressDialogContainer(_context);
	}

	@Override
	protected void onPreExecute() {
		performCustomActionsOnPreExecute();
	}

	protected void performCustomActionsOnPreExecute() {
		container.showProgress();
	}

	@Override
	protected JsonElement doInBackground(Object... token) {

		try {
			String currentToken = token[0].toString();
			String userName = token[1].toString();
			int pageNumber = (int) token[2];
			return CreateAndExecuteService(currentToken, userName, pageNumber);
		} catch (Exception e) {
			performCustomActionsOnException();
			ActivityUtils.HandleConnectionUnsuccessfullToServer(context);
			return null;
		}
	}

	protected void performCustomActionsOnException() {
		container.dismissProgress();
	}

	protected abstract JsonElement CreateAndExecuteService(String currentToken, String userName, int pageNumber)
			throws Exception;

	@Override
	protected void onPostExecute(JsonElement result) {
		performCustomActionsOnPostExecute();
		if (result == null) {
			return;
		}

		JsonObject resultJsonObject = result.getAsJsonObject();

		boolean recipiesFound = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

		if (recipiesFound) {
			JsonElement dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
			JsonArray resultDataObject = dataElement.getAsJsonArray();

			Gson gson = new GsonBuilder().create();

			details = gson.fromJson(resultDataObject, type);

		}

	}

	protected void performCustomActionsOnPostExecute() {
		container.dismissProgress();
	}

}
