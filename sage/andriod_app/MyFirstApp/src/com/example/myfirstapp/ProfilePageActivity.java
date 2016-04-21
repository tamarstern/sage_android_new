package com.example.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sage.adapters.NewsfeedArrayAdapter;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.UserFollowingContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.User;
import com.sage.services.GetPublishedRecipesForUser;
import com.sage.tasks.GetRecipiesActivity;
import com.sage.utils.AnalyticsUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class ProfilePageActivity extends AppCompatActivity {

	private ArrayList<RecipeDetails> details;

	private boolean currentUserProfile;

	private ProgressBar progressBar;

	private MenuItem followUserMenuItem;

	private MenuItem unfollowUserMenuItem;

	private String userDisplayName;

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

			adapter = new NewsfeedArrayAdapter(this, new ArrayList<RecipeDetails>());
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
		User user = createUserObject();
		UserFollowingContainer.getInstance().follow(user);
		initFollowButtonsVisibility(true);
		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PRESS_FOLLOW_USER);

	}

	@NonNull
	private User createUserObject() {
		Intent i = getIntent();
		User user = new User();
		user.setUserDisplayName(this.userDisplayName);
		String userId = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
		user.set_id(userId);
		String username = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER);
		user.setUsername(username);
		return user;
	}

	private void unfollowUser() {
		User user = createUserObject();
		UserFollowingContainer.getInstance().unFollow(user);
		initFollowButtonsVisibility(false);
		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PRESS_UNFOLLOW_USER);

	}

	private void getAllRecipiesForUser() {
		ArrayList<RecipeDetails> recipesByPage = MyProfileRecipiesContainer.getInstance().getRecipesByPage(pageNumber);
		if( recipesByPage!= null ) {
			initAdaptor(recipesByPage);
			return;
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		Intent i = getIntent();
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		String recipeAuthor = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
		recipeAuthor = TextUtils.isEmpty(recipeAuthor) ? userName : recipeAuthor;
		new GetRecipiesTask<RecipeDetails>(this, recipeAuthor).execute(token, userName, pageNumber);
	}

	private void InitProfileDisplay() {

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		Intent i = getIntent();
		Serializable transferedUserDisplayName = i
				.getSerializableExtra(EntityDataTransferConstants.USER_DISPLAY_NAME_DATA_TRANSFER);
		iniActionBarWithUserName(sharedPref, transferedUserDisplayName);
		if (!currentUserProfile) {
			boolean isFollowing = (boolean)i.getSerializableExtra(EntityDataTransferConstants.IS_FOLLOWING);
			initFollowButtonsVisibility(isFollowing);
		}

		else {

			Serializable countTransfered = getIntent().getSerializableExtra(EntityDataTransferConstants.FOLLOW_BY_COUNT);
			String count = (countTransfered == null ? "" :countTransfered.toString()) ;

			initSupportActionBarTitleWithFollowersNumber(count);
		}

	}

	private void iniActionBarWithUserName(SharedPreferences sharedPref, Serializable transferedUserDisplayName) {
		ActionBar supportActionBar = getSupportActionBar();
		if (transferedUserDisplayName != null) {
			this.userDisplayName = transferedUserDisplayName.toString();
			supportActionBar.setTitle(userDisplayName);

		} else {
			userDisplayName = sharedPref.getString(ActivityConstants.USER_DISPLAY_NAME, null);
			supportActionBar.setTitle(userDisplayName);
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

	private void initSupportActionBarTitleWithFollowersNumber(String followedByCount) {
		if (TextUtils.isEmpty(followedByCount)) {
			return;
		}

		String currentText = getResources().getString(R.string.user_followed_by);
		String followedByText = MessageFormat.format("{0}: {1}", currentText, followedByCount);
		ActionBar supportActionBar = getSupportActionBar();
		String currentTitle = supportActionBar.getTitle().toString();
		currentTitle = MessageFormat.format("{0}   ({1})", currentTitle, followedByText);
		supportActionBar.setTitle(currentTitle);
	}

	private void initAdaptor(ArrayList<RecipeDetails> details) {
		Iterator iterator = details.iterator();
		while (iterator.hasNext()) {
			adapter.add((RecipeDetails) iterator.next());
		}
		adapter.notifyDataSetChanged();
		if(shouldIncreasePage) {
			pageNumber += 1;
		}
	}



	private class GetRecipiesTask<T extends RecipeDetails> extends GetRecipiesActivity {

		private String recipeAuthor;

		GetRecipiesTask(Activity context, String recipeAuthor) {
			super(new TypeToken<ArrayList<RecipeDetails>>() {
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
			if (listView.getAdapter().getCount()-1 > 0) {
				progressBar.setVisibility(View.VISIBLE);
			} else {
				super.performCustomActionsOnPreExecute();
			}

		}

		@Override
		protected void performCustomActionsOnException() {

			loadingMore = false;
			shouldIncreasePage = false;
			if (listView.getAdapter().getCount()-1 > 0) {
				progressBar.setVisibility(View.GONE);
			} else {
				super.performCustomActionsOnException();
			}
		}

		@Override
		protected void performCustomActionsOnPostExecute() {
			loadingMore = false;
			if (listView.getAdapter().getCount()-1 > 0) {
				progressBar.setVisibility(View.GONE);
			} else {
				super.performCustomActionsOnPostExecute();
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			super.onPostExecute(result);
			if (details != null) {
				MyProfileRecipiesContainer.getInstance().putRecipesForPage(pageNumber, details);
				initAdaptor(details);
				
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

}
