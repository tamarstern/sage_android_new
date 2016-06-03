package com.sage.activities;

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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.application.UserFollowingContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.GetFollowingService;
import com.sage.tasks.BaseFetchUsersTask;
import com.sage.activities.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayFollowingActivity extends AppCompatActivity {

	private ListView listView;
	private int pageNumber;
	private RelativeLayout failedToLoadPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_following);

		listView = (ListView) findViewById(android.R.id.list);

		failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				fetchUsers();
			}
		});


		initSupportActionBar();
		fetchUsers();
	}

	private void initAdapter(List<User> users) {
		Collections.sort(users);
		UsersArrayAdapter adapter = new UsersArrayAdapter(this, users);
		listView.setAdapter(adapter);
	}


	private void fetchUsers() {

		Intent intent = getIntent();
		String userName = (String) intent.getSerializableExtra(ActivityConstants.USER_OBJECT_ID);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String loggedInUserObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		boolean isLoggedInUser = userName.equals(loggedInUserObjectId);

		if(isLoggedInUser && UserFollowingContainer.getInstance().followingInitialized()) {
			ArrayList<User> users = UserFollowingContainer.getInstance().getUsers();
			initAdapter(users);
			return;
		}

		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String[] params = new String[] { token, userName, Integer.toString(pageNumber) };

		new GetFollowingTask(isLoggedInUser,this).execute(params);
	}

	private void initSupportActionBar() {
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		getSupportActionBar().setTitle(R.string.who_i_follow_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.display_who_i_follow_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_search_user) {
			Intent intent = new Intent(this, SearchMyFollowActivity.class);
			startActivity(intent);
			return true;
		} else {

			return super.onOptionsItemSelected(item);

		}
	}

	private class GetFollowingTask extends BaseFetchUsersTask {

		private Activity activity;
		private ProgressDialogContainer container;
		private boolean isLoggedInUser;

		public GetFollowingTask(boolean isLoggedInUser, Activity activity) {
			super(activity);
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
			this.isLoggedInUser = isLoggedInUser;
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement createAndExecuteService(String currentToken, String userName, int pageNumber)
				throws Exception {
			GetFollowingService service = new GetFollowingService(currentToken, userName, pageNumber);
			return service.getUsers();

		}

		@Override
		protected void handleNoUsersFound() {

		}

		@Override
		protected void initializeUi(List<User> users) {
			initAdapter(users);
			if(isLoggedInUser) {
				UserFollowingContainer.getInstance().putUsers(new ArrayList<User>(users));
			}

		}

		@Override
		protected void performCustomActionsOnException() {
			container.dismissProgress();
			failedToLoadPanel.setVisibility(View.VISIBLE);
		}

		@Override
		protected void performCustomActionsOnPostExecute() {
			container.dismissProgress();
		}

	}

}
