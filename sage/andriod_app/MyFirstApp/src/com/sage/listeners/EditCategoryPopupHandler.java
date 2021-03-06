package com.sage.listeners;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.activities.ProgressDialogContainer;
import com.sage.activities.R;
import com.sage.activity.interfaces.ICategoryEditListener;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeCategory;
import com.sage.services.DeleteCategoryService;
import com.sage.services.SaveNewCategoryService;
import com.sage.services.UpdateCategoryService;
import com.sage.tasks.BaseDeleteCategoryTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;

import java.util.HashSet;
import java.util.Set;

public class EditCategoryPopupHandler {

	private LayoutInflater inflater;
	private ViewGroup container;

	private RecipeCategory category;

	private EditText categoryName;

	private final Activity context;

	private Set<ICategoryEditListener> listeners = new HashSet<ICategoryEditListener>();

	public EditCategoryPopupHandler(LayoutInflater inflater, ViewGroup container, Activity context) {
		this.inflater = inflater;
		this.container = container;
		this.context = context;

	}

	public void handleEditCategory(RecipeCategory category) {
		this.category = category;
		View popupView = inflater.inflate(R.layout.edit_category_popup, container, false);
		Rect displayRectangle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		final PopupWindow popupWindow = new PopupWindow(popupView, (int) (displayRectangle.width() * 0.8f),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindow.showAtLocation(container, Gravity.CENTER, 0, 0);

		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);
		this.categoryName = (EditText) popupView.findViewById(R.id.category_name_editable);

		categoryName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					ActivityUtils.hideSoftKeyboard(context);
					saveCategory(getCategory(), popupWindow);
					return true;
				}
				return false;
			}
		});

		if(!TextUtils.isEmpty(category.getHeader())) {
			categoryName.setText(category.getHeader());
		}

		initSaveButton(popupView, popupWindow, category);

		initCancelSaveButton(popupView, popupWindow);

		initDeleteButton(popupView, popupWindow);

	}

	private void initDeleteButton(View popupView, final PopupWindow popupWindow) {
		Button deleteCategory = (Button) popupView.findViewById(R.id.delete_category);
		deleteCategory.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

				String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

				Object[] params = new Object[] { getCategory().get_id(), token, userName };

				new DeleteCategoryTask(context, popupWindow).execute(params);

			}
		});
		
		if(EntityUtils.isNewCategory(getCategory())) {
			deleteCategory.setVisibility(View.GONE);
		}

	}

	private void initSaveButton(View popupView, final PopupWindow popupWindow, final RecipeCategory currentCategory) {
		Button saveCategory = (Button) popupView.findViewById(R.id.save_category);
		saveCategory.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				ActivityUtils.hideSoftKeyboard(context);
				saveCategory(currentCategory, popupWindow);
			}

		});
	}

	private void saveCategory(RecipeCategory currentCategory, PopupWindow popupWindow) {
		currentCategory.setHeader(categoryName.getText().toString());
		SaveCategory(currentCategory, popupWindow);
	}

	private void initCancelSaveButton(View popupView, final PopupWindow popupWindow) {
		Button cancelSave = (Button) popupView.findViewById(R.id.cancel_save_category);
		cancelSave.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				popupWindow.dismiss();
			}
		});
	}


	private void SaveCategory(RecipeCategory category, PopupWindow popupWindow) {
		
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		Object[] params = new Object[] { category, token, userName };

		new SaveCategoryTask(popupWindow).execute(params);
		
		AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.SAVE_CATEGORY);

	}

	public void notifySaveCategory(RecipeCategory category) {
		for (ICategoryEditListener listener : listeners) {
			listener.onSaveCategory(category);
		}
	}

	public void notifyDeleteCategory(RecipeCategory category) {
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

	public RecipeCategory getCategory() {
		return category;
	}

	private class SaveCategoryTask extends AsyncTask<Object, Void, JsonElement> {

		private PopupWindow popupWindow;

		private ProgressDialogContainer container;

		public SaveCategoryTask(PopupWindow popupWindow) {
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
				RecipeCategory categoryToSave = (RecipeCategory) params[0];
				String token = (String) params[1];
				String userName = (String) params[2];

				if (EntityUtils.isNewCategory(categoryToSave)) {
					SaveNewCategoryService service = new SaveNewCategoryService(categoryToSave, token, userName, context);
					return service.saveNewCategory();

				} else {
					UpdateCategoryService service = new UpdateCategoryService(categoryToSave, token, userName,context);
					return service.updateCategory();

				}

			} catch (Exception e) {
				container.dismissProgress();
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
			boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			if (saveSucceed) {

				Gson gson = new Gson();

				JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

				RecipeCategory category = gson.fromJson(dataElement, RecipeCategory.class);

				notifySaveCategory(category);

				popupWindow.dismiss();
			}

		}

	}

	private class DeleteCategoryTask extends BaseDeleteCategoryTask {

		private PopupWindow popupWindow;

		public DeleteCategoryTask(Activity activity, PopupWindow popupWindow) {
			super(activity);
			this.popupWindow = popupWindow;
		}

		@Override
		protected JsonElement createAndExecuteService(String categoryId, String token, String userName)
				throws Exception {
			DeleteCategoryService service = new DeleteCategoryService(categoryId, token, userName, context);
			return service.deleteCategory();
		}

		@Override
		protected void handleSuccess(JsonObject dataElement) {
			Gson gson = new Gson();

			RecipeCategory category = gson.fromJson(dataElement, RecipeCategory.class);

			notifyDeleteCategory(category);

			popupWindow.dismiss();

		}
		
		protected void handleFailure() {
			Resources res = context.getResources();
			final String notInternetConnectionText = res.getString(R.string.attached_recipes_to_category);
			
			context.runOnUiThread(new Runnable() {
				  public void run() {
					  Toast.makeText(context, notInternetConnectionText, Toast.LENGTH_LONG).show();
				  }
				});
		}

	}

}
