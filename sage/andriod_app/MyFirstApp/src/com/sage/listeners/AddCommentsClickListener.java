package com.sage.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.myfirstapp.ProgressDialogContainer;
import com.example.myfirstapp.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sage.activity.interfaces.IClosePopupCommentListener;
import com.sage.adapters.CommentsArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeComment;
import com.sage.services.GetCommentsForRecipeService;
import com.sage.services.SaveNewCommentService;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AddCommentsClickListener implements OnClickListener {

	private LayoutInflater inflater;
	private ViewGroup container;
	private View parent;
	private String recipeId;
	private Activity activity;
	private ListView listView;
	private ImageButton addCommentsButton;
	private EditText commentTextBox;
	private PopupWindow popupWindow;
	private Button closeCommentsPopup;
	private Context popupWindowContext;

	private RelativeLayout failedToLoadPanel;

	private Set<IClosePopupCommentListener> listeners = new HashSet<IClosePopupCommentListener>();

	public AddCommentsClickListener(LayoutInflater inflater, ViewGroup container, View parent, String recipeId,
			Activity context) {
		this.inflater = inflater;
		this.container = container;
		this.parent = parent;
		this.recipeId = recipeId;
		this.activity = context;
	}

	@Override
	public void onClick(View v) {
		View popupView = inflater.inflate(R.layout.add_comment_popup, container, false);
		listView = (ListView) popupView.findViewById(android.R.id.list);
		Rect displayRectangle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		popupWindow = new PopupWindow(popupView, (int) (displayRectangle.width() * 0.9f),
				(int) (displayRectangle.height() * 0.9f), true);
		popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

		popupWindowContext = popupWindow.getContentView().getContext();

		closeCommentsPopup = (Button) popupView.findViewById(R.id.close_comments_popup);
		closeCommentsPopup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				notifyCommentsPopupClosed();
			}
		});

		commentTextBox = (EditText) popupView.findViewById(R.id.add_comment);

		addCommentsButton = (ImageButton) popupView.findViewById(R.id.add_comment_request);

		addCommentsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String commentText = commentTextBox.getText().toString();
				if (!TextUtils.isEmpty(commentText)) {
					addComment(commentText);
				}

			}
		});

		failedToLoadPanel = (RelativeLayout)popupView.findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				getCommentsForRecipe(popupWindowContext);
			}
		});


		getCommentsForRecipe(popupWindowContext);

		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, activity);

	}

	private void initCommentUserTouchUps(RecipeComment comment) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
		String userDisplayName = sharedPref.getString(ActivityConstants.USER_DISPLAY_NAME, null);
		comment.setUserDisplayName(userDisplayName);

		String userObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		comment.setUserObjectId(userObjectId);

		String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);
		comment.setUserId(userName);

	}

	private void addComment(String commentText) {
		addNewCommentToAdapter(commentText);

		AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.ADD_COMMENT);

		commentTextBox.getText().clear();

		saveCommentInServer(commentText);
	}

	private void saveCommentInServer(String commentText) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		Object[] params = new Object[] { token, userName, recipeId, commentText };

		new AddCommentTask().execute(params);
	}

	private void addNewCommentToAdapter(String commentText) {
		RecipeComment comment = new RecipeComment();
		comment.set_id(UUID.randomUUID().toString());
		comment.setText(commentText);
		initCommentUserTouchUps(comment);
		ArrayAdapter<RecipeComment> adapter = (ArrayAdapter<RecipeComment>)listView.getAdapter();
		adapter.insert(comment, 0);
	}

	private void getCommentsForRecipe(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		Object[] params = new Object[] { token, recipeId };

		new GetCommentsTask(context).execute(params);
	}


	public void notifyCommentsPopupClosed() {
		for (IClosePopupCommentListener listener : listeners) {
			listener.onClosePopupComment(listView.getCount(), recipeId);
		}
	}

	public void registerListener(IClosePopupCommentListener listener) {
		listeners.add(listener);

	}

	public void unRegisterListener(IClosePopupCommentListener listener) {
		listeners.remove(listener);

	}

	private class GetCommentsTask extends AsyncTask<Object, Void, JsonElement> {

		private ProgressDialogContainer container;

		public GetCommentsTask(Context context) {
			container = new ProgressDialogContainer(context);
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement doInBackground(Object... params) {

			try {
				String token = (String) params[0];
				String recipeId = (String) params[1];

				GetCommentsForRecipeService service = new GetCommentsForRecipeService(token, recipeId);

				return service.getComments();

			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			container.dismissProgress();
			if (result == null) {
				failedToLoadPanel.setVisibility(View.VISIBLE);
				return;
			}
			JsonObject resultJsonObject = result.getAsJsonObject();
			boolean success = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			if (success) {

				JsonArray commentsArray = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonArray();

				Gson gson = new GsonBuilder().create();

				ArrayList<RecipeComment> comments = gson.fromJson(commentsArray,
						new TypeToken<ArrayList<RecipeComment>>() {
						}.getType());

				CommentsArrayAdapter adapter = new CommentsArrayAdapter(activity, comments);
				listView.setAdapter(adapter);
				ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, activity);

			}

		}

	}

	private class AddCommentTask extends AsyncTask<Object, Void, JsonElement> {


		public AddCommentTask() {

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected JsonElement doInBackground(Object... params) {

			try {
				String token = (String) params[0];
				String userName = (String) params[1];

				String recipeId = (String) params[2];
				String textComment = (String) params[3];

				SaveNewCommentService service = new SaveNewCommentService(token, userName, recipeId, textComment, activity);
				return service.saveComment();

			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {

		}

	}

}
