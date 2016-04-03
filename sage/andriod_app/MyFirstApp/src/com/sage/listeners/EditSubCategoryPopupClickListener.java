package com.sage.listeners;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.example.myfirstapp.ProgressDialogContainer;
import com.example.myfirstapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.activity.interfaces.ICategoryEditListener;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeCategoryBase;
import com.sage.entities.RecipeSubCategory;
import com.sage.services.DeleteSubCategoryService;
import com.sage.services.SaveNewSubCategoryService;
import com.sage.services.UpdateSubCategoryService;
import com.sage.tasks.BaseDeleteCategoryTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

public class EditSubCategoryPopupClickListener implements OnClickListener {

	private LayoutInflater inflater;
	private ViewGroup container;

	private RecipeSubCategory category;

	private EditText categoryName;

	private final Activity context;

	private Set<ICategoryEditListener> listeners = new HashSet<ICategoryEditListener>();

	public EditSubCategoryPopupClickListener(LayoutInflater inflater, ViewGroup container, RecipeSubCategory category,
			Activity context) {
		this.inflater = inflater;
		this.container = container;
		this.category = category;
		this.context = context;

	}

	@Override
	public void onClick(View v) {
		View popupView = inflater.inflate(R.layout.edit_category_popup, container, false);
		Rect displayRectangle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		final PopupWindow popupWindow = new PopupWindow(popupView, (int) (displayRectangle.width() * 0.8f),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindow.showAtLocation(container, Gravity.CENTER, 0, 0);
		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);
		this.categoryName = (EditText) popupView.findViewById(R.id.category_name_editable);

		final RecipeCategoryBase currentCategory = getCategoryToEdit();

		Button saveCategory = (Button) popupView.findViewById(R.id.save_category);
		saveCategory.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				currentCategory.setHeader(categoryName.getText().toString());
				SaveCategory(currentCategory, popupWindow);
			}

		});

		Button cancelSave = (Button) popupView.findViewById(R.id.cancel_save_category);
		cancelSave.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				popupWindow.dismiss();
			}
		});

		initDeleteButton(popupView, popupWindow);

	}

	private void initDeleteButton(View popupView, final PopupWindow popupWindow) {
		Button deleteCategory = (Button) popupView.findViewById(R.id.delete_category);
		deleteCategory.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

				String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

				Object[] params = new Object[] { category.get_id(), token, userName };

				new DeleteSubCategoryTask(context, popupWindow).execute(params);

			}
		});
		if (EntityUtils.isNewCategory(category)) {
			deleteCategory.setVisibility(View.GONE);
		}

	}

	private RecipeSubCategory getCategoryToEdit() {
		final RecipeSubCategory currentCategory;

		currentCategory = category;
		categoryName.setText(currentCategory.getHeader());

		return currentCategory;
	}

	private void SaveCategory(RecipeCategoryBase category, PopupWindow popupWindow) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		Object[] params = new Object[] { category, token, userName };

		new SaveSubCategoryTask(popupWindow).execute(params);
		AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.SAVE_SUB_CATEGORY);

	}

	public void notifySaveCategory(RecipeCategoryBase category) {
		for (ICategoryEditListener listener : listeners) {
			listener.onSaveCategory(category);
		}
	}

	public void notifyDeleteCategory(RecipeCategoryBase category) {
		for (ICategoryEditListener listener : listeners) {
			listener.onDeleteCategory(category);
		}
	}

	public void registerListener(ICategoryEditListener listener) {
		listeners.add(listener);

	}

	public void unRegisterListener(ICategoryEditListener listener) {
		listeners.remove(listener);

	}

	private class SaveSubCategoryTask extends AsyncTask<Object, Void, JsonElement> {

		private PopupWindow popupWindow;

		private ProgressDialogContainer container;

		public SaveSubCategoryTask(PopupWindow popupWindow) {
			this.popupWindow = popupWindow;
			container = new ProgressDialogContainer(context);
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement doInBackground(Object... params) {

			try {
				RecipeSubCategory subCategoryToSave = (RecipeSubCategory) params[0];
				String token = (String) params[1];
				String userName = (String) params[2];

				if (EntityUtils.isNewCategory(subCategoryToSave)) {
					SaveNewSubCategoryService service = new SaveNewSubCategoryService(subCategoryToSave, token,
							userName, context);
					return service.saveNewCategory();
				} else {
					UpdateSubCategoryService service = new UpdateSubCategoryService(subCategoryToSave, token, userName,
							context);
					return service.updateSubCategory();
				}

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
			boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			if (saveSucceed) {

				Gson gson = new Gson();

				JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

				RecipeSubCategory category = gson.fromJson(dataElement, RecipeSubCategory.class);

				notifySaveCategory(category);

				popupWindow.dismiss();
			}

		}

	}

	private class DeleteSubCategoryTask extends BaseDeleteCategoryTask {

		private PopupWindow popupWindow;
		private Activity activity;

		public DeleteSubCategoryTask(Activity activity, PopupWindow popupWindow) {
			super(activity);
			this.activity = activity;
			this.popupWindow = popupWindow;
		}

		@Override
		protected JsonElement createAndExecuteService(String categoryId, String token, String userName)
				throws Exception {
			DeleteSubCategoryService service = new DeleteSubCategoryService(categoryId, token, userName, activity);
			return service.deleteSubCategory();
		}

		@Override
		protected void handleSuccess(JsonObject dataElement) {
			Gson gson = new Gson();

			RecipeSubCategory category = gson.fromJson(dataElement, RecipeSubCategory.class);

			notifyDeleteCategory(category);

			popupWindow.dismiss();

		}

		protected void handleFailure() {
			Resources res = context.getResources();
			final String notInternetConnectionText = res.getString(R.string.attached_recipes_to_sub_category);

			context.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(context, notInternetConnectionText, Toast.LENGTH_LONG).show();
				}
			});
		}

	}

}
