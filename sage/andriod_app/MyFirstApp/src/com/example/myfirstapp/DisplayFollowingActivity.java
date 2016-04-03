package com.example.myfirstapp;

import java.util.List;

import com.google.gson.JsonElement;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.GetFollowingService;
import com.sage.tasks.BaseFetchUsersTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class DisplayFollowingActivity extends AppCompatActivity {

	private ListView listView;
	private int pageNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_following);

		listView = (ListView) findViewById(android.R.id.list);

		initSupportActionBar();
		fetchUsers();
	}

	private void fetchUsers() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		Intent intent = getIntent();
		String userName = (String) intent.getSerializableExtra(ActivityConstants.USER_OBJECT_ID);

		String[] params = new String[] { token, userName, Integer.toString(pageNumber) };

		new GetFollowingTask(this).execute(params);
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

		public GetFollowingTask(Activity activity) {
			super(activity);
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
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
		protected void initializeUi(List<User> users) {
			UsersArrayAdapter adapter = new UsersArrayAdapter(activity, users);
			listView.setAdapter(adapter);

		}

		@Override
		protected void performCustomActionsOnException() {
			container.dismissProgress();

		}

		@Override
		protected void performCustomActionsOnPostExecute() {
			container.dismissProgress();
		}

	}

}
