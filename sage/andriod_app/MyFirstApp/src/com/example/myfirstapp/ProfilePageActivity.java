package com.example.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sage.adapters.NewsfeedArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeBasicData;
import com.sage.entities.RecipeUserBasicData;
import com.sage.services.FollowUserService;
import com.sage.services.GetMyProfile;
import com.sage.services.GetProfileById;
import com.sage.services.GetPublishedRecipesForUser;
import com.sage.services.UnfollowUserService;
import com.sage.tasks.GetRecipiesActivity;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ProfilePageActivity extends AppCompatActivity {

	private ArrayList<RecipeBasicData> details;

	private boolean currentUserProfile;

	private ProgressBar progressBar;

	private MenuItem followUserMenuItem;

	private MenuItem unfollowUserMenuItem;

	private ListView listView;

	private int pageNumber = 0;
	private int preLast = 0;;
	private volatile boolean loadingMore = false;
	private boolean afterStop;
	private boolean shouldIncreasePage = true;

	private NewsfeedArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_page);

		View footer = getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
		progressBar = (ProgressBar) footer.findViewById(R.id.get_recipies_progress);

		progressBar.setVisibility(View.GONE);
		listView = (ListView) findViewById(android.R.id.list);
		listView.addFooterView(footer);
		initListView();

		Intent i = getIntent();
		currentUserProfile = (boolean) i.getBooleanExtra(EntityDataTransferConstants.OPEN_USER_PROFILE, false);

		initializeSuportBar();

		if (currentUserProfile) {
			AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.OPEN_MY_PROFILE);
		} else {
			AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.VIEW_OTHER_USER_PROFILE);
		}

	}

	@Override
	protected void onStop() {
		this.afterStop = true;
		super.onStop();
	}

	private void initListView() {
		if (listView.getAdapter() == null) {

			adapter = new NewsfeedArrayAdapter(this, new ArrayList<RecipeUserBasicData>());
			listView.setAdapter(adapter);
		}

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				if (afterStop) {
					afterStop = false;
					return;
				}

				if (loadingMore) {
					return;
				}
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if ((lastInScreen == totalItemCount)) {
					if (preLast == 0 || preLast != lastInScreen) {
						preLast = lastInScreen;
						getAllRecipiesForUser();
					}

				}
			}
		});
	}

	private void initializeSuportBar() {
		Toolbar myToolbar = (Toolbar) findViewById(R.id.title_profile_page);
		setSupportActionBar(myToolbar);
		getSupportActionBar().setTitle("");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (currentUserProfile) {
			getMenuInflater().inflate(R.menu.menu_my_profile, menu);
		} else {
			getMenuInflater().inflate(R.menu.user_profile_activity_menu, menu);
			followUserMenuItem = menu.findItem(R.id.action_follow);
			unfollowUserMenuItem = menu.findItem(R.id.action_unfollow);
		}

		InitProfileDisplay();

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_follow) {
			followUser();
			return true;
		}
		if (item.getItemId() == R.id.action_unfollow) {
			unfollowUser();
			return true;
		}
		if (item.getItemId() == R.id.action_who_i_follow) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String userObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
			if (!currentUserProfile) {
				Intent i = getIntent();
				userObjectId = (String) i
						.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
			}
			Intent intent = new Intent(this, DisplayFollowingActivity.class).putExtra(ActivityConstants.USER_OBJECT_ID,
					userObjectId);
			startActivity(intent);
			return true;

		} else {

			return super.onOptionsItemSelected(item);

		}
	}

	private void followUser() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		Intent i = getIntent();
		String usernameToFollow = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER);
		String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);

		new FollowUser(this).execute(token, userName, usernameToFollow);

		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PRESS_FOLLOW_USER);

	}

	private void unfollowUser() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		Intent i = getIntent();
		String usernameToFollow = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER);
		String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);

		new UnFollowUser(this).execute(token, userName, usernameToFollow);

		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PRESS_UNFOLLOW_USER);

	}

	private void getAllRecipiesForUser() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		Intent i = getIntent();
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		String recipeAuthor = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
		recipeAuthor = TextUtils.isEmpty(recipeAuthor) ? userName : recipeAuthor;
		new GetRecipiesTask<RecipeUserBasicData>(this, recipeAuthor).execute(token, userName, pageNumber);
	}

	private void InitProfileDisplay() {

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		Intent i = getIntent();
		Serializable transferedUserDisplayName = i
				.getSerializableExtra(EntityDataTransferConstants.USER_DISPLAY_NAME_DATA_TRANSFER);
		iniActionBarWithUserName(sharedPref, transferedUserDisplayName);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String userObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		if (!currentUserProfile) {
			String userToFolloId = (String) i
					.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
			new GetUserProfile(this).execute(token, userObjectId, userToFolloId);
		}

		else {

			new GetMyProfileTask().execute(token, userObjectId);
		}

	}

	private void iniActionBarWithUserName(SharedPreferences sharedPref, Serializable transferedUserDisplayName) {
		if (transferedUserDisplayName != null) {
			String userDisplayName = transferedUserDisplayName.toString();
			getSupportActionBar().setTitle(userDisplayName);

		} else {
			String loogeDInUserDisplayName = sharedPref.getString(ActivityConstants.USER_DISPLAY_NAME, null);
			getSupportActionBar().setTitle(loogeDInUserDisplayName);
		}
	}

	private void initFollowButtonsVisibility(boolean isFollowing) {
		if (isFollowing) {
			followUserMenuItem.setVisible(false);
			unfollowUserMenuItem.setVisible(true);
		} else {
			followUserMenuItem.setVisible(true);
			unfollowUserMenuItem.setVisible(false);
		}
	}

	private void initVisibilityForServerError() {
		initFollowButtonsVisibility(false);
	}

	private void initSupportActionBarTitleWithFollowersNumber(String follwedByCount) {
		String currentText = getResources().getString(R.string.user_followed_by);
		String followedByText = MessageFormat.format("{0}: {1}", currentText, follwedByCount);
		String currentTitle = getSupportActionBar().getTitle().toString();
		currentTitle = MessageFormat.format("{0}   ({1})", currentTitle, followedByText);
		getSupportActionBar().setTitle(currentTitle);
	}

	private class GetRecipiesTask<T extends RecipeUserBasicData> extends GetRecipiesActivity {

		private String recipeAuthor;

		GetRecipiesTask(Activity context, String recipeAuthor) {
			super(new TypeToken<ArrayList<RecipeUserBasicData>>() {
			}.getType(), context);
			this.recipeAuthor = recipeAuthor;
		}

		@Override
		protected JsonElement doInBackground(Object... token) {
			loadingMore = true;
			return super.doInBackground(token);
		}

		@Override
		protected void performCustomActionsOnPreExecute() {
			shouldIncreasePage = true;
			if (listView.getAdapter().getCount() > 0) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				super.performCustomActionsOnPreExecute();
			}

		}

		@Override
		protected void performCustomActionsOnException() {

			loadingMore = false;
			shouldIncreasePage = false;
			if (listView.getAdapter().getCount() > 0) {
				progressBar.setVisibility(View.GONE);
			} else {
				super.performCustomActionsOnException();
			}
		}

		@Override
		protected void performCustomActionsOnPostExecute() {
			loadingMore = false;
			if (listView.getAdapter().getCount() > 0) {
				progressBar.setVisibility(View.GONE);
			} else {
				super.performCustomActionsOnPostExecute();
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			super.onPostExecute(result);
			if (details != null) {
				Iterator iterator = details.iterator();
				while (iterator.hasNext()) {
					adapter.add((RecipeUserBasicData) iterator.next());
				}
				adapter.notifyDataSetChanged();
				if(shouldIncreasePage) {
					pageNumber += 1;
				}
				
				loadingMore = false;

			}

		}

		@Override
		protected JsonElement CreateAndExecuteService(String currentToken, String userName, int pageNumber)
				throws Exception {
			GetPublishedRecipesForUser service = new GetPublishedRecipesForUser(currentToken, userName, recipeAuthor,
					pageNumber);
			return service.getRecipies();
		}

	}

	private class GetUserProfile extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;

		public GetUserProfile(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected JsonElement doInBackground(String... params) {

			try {
				String currentToken = params[0];
				String userName = params[1];
				String userToFollow = params[2];
				GetProfileById service = new GetProfileById(currentToken, userName, userToFollow);

				return service.getProfile();
			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(activity);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {

			if (result == null) {
				initVisibilityForServerError();
				return;
			}

			JsonObject resultJsonObject = result.getAsJsonObject();

			boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

			if (requestSuccess) {
				boolean isFollowing = resultJsonObject.get(ActivityConstants.IS_FOLLOWING).getAsBoolean();

				initFollowButtonsVisibility(isFollowing);
			}
		}

	}

	private class GetMyProfileTask extends AsyncTask<String, Void, JsonElement> {

		@Override
		protected JsonElement doInBackground(String... params) {
			try {
				String currentToken = params[0];
				String userName = params[1];
				GetMyProfile service = new GetMyProfile(currentToken, userName);

				return service.getMyProfile();
			} catch (Exception e) {
				Log.d(CONNECTIVITY_SERVICE, "Unable to retrieve web page. URL may be invalid.");
				return null;
			}

		}

		@Override
		protected void onPostExecute(JsonElement result) {
			if (result == null) {
				String follwedByCount = NumberFormat.getNumberInstance(Locale.US)
						.format(ActivityConstants.EMPTY_FOLLOW_BY_COUNT);
				initSupportActionBarTitleWithFollowersNumber(follwedByCount);
				return;
			}

			JsonObject resultJsonObject = result.getAsJsonObject();

			boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

			if (requestSuccess) {
				String follwedByCount = NumberFormat.getNumberInstance(Locale.US)
						.format(resultJsonObject.get(ActivityConstants.FOLLOEWD_BY_COUNT).getAsInt());

				initSupportActionBarTitleWithFollowersNumber(follwedByCount);
			}

		}

	}

	private class FollowUser extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;

		public FollowUser(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected JsonElement doInBackground(String... params) {

			try {
				String currentToken = params[0];
				String userName = params[1];
				String usernameToFollow = params[2];
				FollowUserService service = new FollowUserService(currentToken, userName, usernameToFollow, activity);
				return service.followUser();
			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(activity);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			if (result == null) {
				return;
			}
			JsonObject resultJsonObject = result.getAsJsonObject();
			boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			if (requestSuccess) {
				initFollowButtonsVisibility(true);
			}

		}

	}

	private class UnFollowUser extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;

		public UnFollowUser(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected JsonElement doInBackground(String... params) {

			try {
				String currentToken = params[0];
				String userName = params[1];
				String usernameToFollow = params[2];
				UnfollowUserService service = new UnfollowUserService(currentToken, userName, usernameToFollow,
						activity);
				return service.unfollowUser();
			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(activity);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			if (result == null) {
				return;
			}
			JsonObject resultJsonObject = result.getAsJsonObject();
			boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			if (requestSuccess) {
				initFollowButtonsVisibility(false);
			}

		}

	}

}
