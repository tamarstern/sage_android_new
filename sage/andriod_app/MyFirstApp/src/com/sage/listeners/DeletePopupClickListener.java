package com.sage.listeners;

import java.io.IOException;

import com.example.myfirstapp.NewsfeedActivity;
import com.example.myfirstapp.ProgressDialogContainer;
import com.example.myfirstapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.DeleteRecipeService;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

public class DeletePopupClickListener implements OnClickListener {

	private LayoutInflater inflater;
	private ViewGroup container;
	private View savePublishRecipe;
	private RecipeDetails recipeDetails;
	private Activity context;

	public DeletePopupClickListener(LayoutInflater inflater, ViewGroup container, View savePublishRecipe,
			RecipeDetails recipeDetails, Activity context) {
		this.inflater = inflater;
		this.container = container;
		this.savePublishRecipe = savePublishRecipe;
		this.recipeDetails = recipeDetails;
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		View popupView = inflater.inflate(R.layout.delete_recipe_popup, container, false);
		Rect displayRectangle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		final PopupWindow popupWindow = new PopupWindow(popupView, (int) (displayRectangle.width() * 0.6f),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindow.showAtLocation(savePublishRecipe, Gravity.CENTER, 0, 0);
		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);

		Button confirmDelete = (Button) popupView.findViewById(R.id.confirm_delete_recipe);
		confirmDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

				String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

				Object[] params = new Object[] { recipeDetails, token, userName };

				new DeleteRecipeTask(context).execute(params);
				
				AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.PRESS_DELETE_RECIPE);
			}
		});

		Button cancelDelete = (Button) popupView.findViewById(R.id.cancel_delete_recipe);
		cancelDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				popupWindow.dismiss();
			}
		});

	}

	private class DeleteRecipeTask extends AsyncTask<Object, Void, JsonElement> {
		
		private Activity context;
		private ProgressDialogContainer container;
		
		public DeleteRecipeTask(Activity context) {
			this.context = context;
			container = new ProgressDialogContainer(context);
		}

		@Override
		protected JsonElement doInBackground(Object... params) {

			try {
				RecipeDetails recipeDetails = (RecipeDetails) params[0];
				String token = (String) params[1];
				String userName = (String) params[2];

				DeleteRecipeService service = new DeleteRecipeService(recipeDetails, token, userName, context);

				return service.deleteRecipe();

			} catch (Exception e) {
				container.dismissProgress();
				ActivityUtils.HandleConnectionUnsuccessfullToServer(context);
				return null;
			}
		}
		@Override
		protected void onPreExecute() {
			container.showProgress();
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

				Intent intent = new Intent(context, NewsfeedActivity.class)
						.putExtra(EntityDataTransferConstants.RECIPE_DELETED, true);
				context.startActivity(intent);
			}

		}

	}

}
