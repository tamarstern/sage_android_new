package com.example.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeUserBasicData;
import com.sage.services.GetNewsFeedRecipesForUser;
import com.sage.tasks.GetRecipiesActivity;
import com.sage.utils.AnalyticsUtils;

import org.apache.http.util.TextUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class NewsfeedActivity extends AppCompatActivity {

	private ListView listView;

	private NewsfeedArrayAdapter adapter;

	private ProgressBar progressBar;

	private int pageNumber = 0;
	private int preLast = 0;;
	private volatile boolean loadingMore = false;

	private boolean afterStop;
	private boolean shouldIncreasePage = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfeed);

		View footer = getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
		progressBar = (ProgressBar) footer.findViewById(R.id.get_recipies_progress);

		progressBar.setVisibility(View.GONE);

		initializeSupportActionBar();

		listView = (ListView) findViewById(android.R.id.list);
		listView.addFooterView(footer);
		initListView();

		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.OPEN_NEWSFEED_ACTIVITY);
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
						getNewsFeedRecipiesForUser();
					}

				}
			}
		});
	}

	private void initializeSupportActionBar() {
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String userDisplayName = sharedPref.getString(ActivityConstants.USER_DISPLAY_NAME, null);
		getSupportActionBar()
				.setTitle(MessageFormat.format("{0} {1} !", getString(R.string.newsfeed_welcome), userDisplayName));

	}

	private void getNewsFeedRecipiesForUser() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		if (TextUtils.isEmpty(userName)) {
			userName = sharedPref.getString(ActivityConstants.USER_NAME, null);
		}

		new GetRecipiesForNewFeed<RecipeUserBasicData>(this).execute(token, userName, pageNumber);

	}

	@Override
	protected void onStop() {
		this.afterStop = true;
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.newsfeed_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_search) {
			Intent searchIntent = new Intent(this, SearchActivity.class);
			startActivity(searchIntent);
			return true;
		} else {

			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		boolean isReceipeDeleted = intent.getSerializableExtra(EntityDataTransferConstants.RECIPE_DELETED) != null;
		if (isReceipeDeleted) {
			return;
		}
		boolean afterLogin = intent.getSerializableExtra(EntityDataTransferConstants.AFTER_LOGIN) != null;
		if (afterLogin) {
			return;
		}
		super.onBackPressed();
	}

	private class GetRecipiesForNewFeed<T extends RecipeUserBasicData> extends GetRecipiesActivity {

		public GetRecipiesForNewFeed(Activity activity) {
			super(new TypeToken<ArrayList<RecipeUserBasicData>>() {
			}.getType(), activity);

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
		protected JsonElement doInBackground(Object... token) {
			loadingMore = true;
			return super.doInBackground(token);
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
				if (shouldIncreasePage) {
					pageNumber += 1;
				}
				loadingMore = false;
			}

		}

		@Override
		protected JsonElement CreateAndExecuteService(String currentToken, String userName, int pageNumber)
				throws Exception {
			GetNewsFeedRecipesForUser service = new GetNewsFeedRecipesForUser(currentToken, userName, pageNumber);
			return service.getRecipies();
		}

	}

}