package com.sage.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sage.adapters.NewsfeedArrayAdapter;
import com.sage.application.NewsfeedContainer;
import com.sage.backgroundServices.BackgroundServicesScheduler;
import com.sage.backgroundServices.GcmRegistrationService;
import com.sage.constants.ActivityConstants;
import com.sage.constants.ServicesConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
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
	private int preLast = 0;
	private volatile boolean loadingMore = false;

	private boolean afterStop;
	private boolean shouldIncreasePage = true;

	private RelativeLayout failedToLoadPanel;

	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private boolean isReceiverRegistered;
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsfeed);

		BackgroundServicesScheduler.scheduleAlarmBackgroundServices(this);

		View footer = getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
		progressBar = (ProgressBar) footer.findViewById(R.id.get_recipies_progress);

		progressBar.setVisibility(View.GONE);

		initializeSupportActionBar();

		failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				getNewsFeedRecipiesForUser();
			}
		});

		handleGcmTokenRegistration();

		listView = (ListView) findViewById(android.R.id.list);
		listView.addFooterView(footer);

		View listHeader =  getLayoutInflater().inflate(R.layout.newsfeed_list_header, null);
		listView.addHeaderView(listHeader);
		initListView();

		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.OPEN_NEWSFEED_ACTIVITY);
	}

	private void handleGcmTokenRegistration() {
		final Activity activity = this;

		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				SharedPreferences sharedPreferences =
						PreferenceManager.getDefaultSharedPreferences(context);
				boolean sentToken = sharedPreferences
						.getBoolean(ServicesConstants.SENT_TOKEN_TO_SERVER, false);
				if (!sentToken) {
					startGcmRegistrationService(activity);
				}
			}
		};

		registerReceiver();

		if (checkPlayServices()) {
			startGcmRegistrationService(this);


		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver();
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		isReceiverRegistered = false;
		super.onPause();
	}


	private void startGcmRegistrationService(Activity activity) {
		Intent intent = new Intent(activity, GcmRegistrationService.class);
		startService(intent);
	}

	private void registerReceiver(){
		if(!isReceiverRegistered) {
			LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
					new IntentFilter(ServicesConstants.REGISTRATION_COMPLETE));
			isReceiverRegistered = true;
		}
	}

	private boolean checkPlayServices() {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i("deviceNotSupported", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
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
		ArrayList<RecipeDetails> recipesByPage = NewsfeedContainer.getInstance().getRecipesByPage(pageNumber);
		if(recipesByPage != null && recipesByPage.size() > 0) {
			initAdaptor(recipesByPage);
			return;
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		if (TextUtils.isEmpty(userName)) {
			userName = sharedPref.getString(ActivityConstants.USER_NAME, null);
		}

		new GetRecipiesForNewFeed<RecipeDetails>(this).execute(token, userName, pageNumber);

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

	private void initAdaptor(ArrayList<RecipeDetails> details) {
		Iterator iterator = details.iterator();
		while (iterator.hasNext()) {
			adapter.add((RecipeDetails) iterator.next());
		}
		adapter.notifyDataSetChanged();
		if (shouldIncreasePage) {
			pageNumber += 1;
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		boolean isReceipeDeleted = intent.getSerializableExtra(EntityDataTransferConstants.RECIPE_DELETED) != null;
		if (isReceipeDeleted) {
			return;
		}
		boolean isNewRecipeUnsaved = intent.getSerializableExtra(EntityDataTransferConstants.NEW_RECIPE_UNSAVED) != null;
		if (isNewRecipeUnsaved) {
			return;
		}
		boolean afterLogin = intent.getSerializableExtra(EntityDataTransferConstants.AFTER_LOGIN) != null;
		if (afterLogin) {
			return;
		}
		super.onBackPressed();
	}

	private class GetRecipiesForNewFeed<T extends RecipeDetails> extends GetRecipiesActivity {

		public GetRecipiesForNewFeed(Activity activity) {
			super(new TypeToken<ArrayList<RecipeDetails>>() {
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
			if(pageNumber == 0) {
				super.performCustomActionsOnException();
				failedToLoadPanel.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else if (listView.getAdapter().getCount()-1 > 0) {
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
				if(pageNumber == 0 || pageNumber ==1) {
					NewsfeedContainer.getInstance().putRecipesForPage(pageNumber, details);
				}
				initAdaptor(details);
				loadingMore = false;
			}

		}

		@Override
		protected void handleNoRecipesFound() {

		}


		@Override
		protected JsonElement CreateAndExecuteService(String currentToken, String userName, int pageNumber)
				throws Exception {
			GetNewsFeedRecipesForUser service = new GetNewsFeedRecipesForUser(currentToken, userName, pageNumber);
			return service.getRecipies();
		}

	}

}