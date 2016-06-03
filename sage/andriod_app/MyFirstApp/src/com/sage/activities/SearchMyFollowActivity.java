package com.sage.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.SearchFollowingService;
import com.sage.tasks.BaseFetchUsersTask;
import com.sage.activities.R;

import java.util.List;

public class SearchMyFollowActivity extends AppCompatActivity {

	private ListView listView;

	private EditText searchEditText;

	private ImageView searchImageView;
	
	private int pageNumber;

	private TextView noUsersMatchCriteria;

	RelativeLayout failedToLoadPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_my_follow);

		listView = (ListView) findViewById(android.R.id.list);

		noUsersMatchCriteria = (TextView) findViewById(R.id.no_users_found_matching_search_criteria);
		noUsersMatchCriteria.setVisibility(View.GONE);
		failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				noUsersMatchCriteria.setVisibility(View.GONE);
				fetchUsers();
			}
		});


		initSupportActionBar();

	}

	private void initSupportActionBar() {		
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
		supportActionBar.setDisplayShowTitleEnabled(false);
		supportActionBar.setDisplayUseLogoEnabled(false);
		supportActionBar.setDisplayHomeAsUpEnabled(false);
		supportActionBar.setDisplayShowCustomEnabled(true);
		supportActionBar.setDisplayShowHomeEnabled(false);

		View customNav = LayoutInflater.from(this).inflate(R.layout.search_my_follow_layout, null);
		supportActionBar.setCustomView(customNav);

		searchEditText = (EditText) customNav.findViewById(R.id.search_text);

		searchImageView = (ImageView) customNav.findViewById(R.id.search_my_follow_icon);

		searchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fetchUsers();
			}

		});
	}

	private void fetchUsers() {
		if (searchEditText.getEditableText() == null
				|| TextUtils.isEmpty(searchEditText.getEditableText().toString())) {
			Toast.makeText(this, R.string.did_not_enter_search_text, Toast.LENGTH_SHORT).show();
			return;
		}
		noUsersMatchCriteria.setVisibility(View.GONE);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		Editable editableText = searchEditText.getEditableText();

		String searchText = editableText.toString();

		String[] params = new String[] { token, userName , Integer.toString(pageNumber)};

		new SearchFollowingTask(this, searchText).execute(params);
	}

	private class SearchFollowingTask extends BaseFetchUsersTask {

		private Activity activity;
		private ProgressDialogContainer container;
		private String searchText;

		public SearchFollowingTask(Activity activity, String searchText) {
			super(activity);
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
			this.searchText = searchText;
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement createAndExecuteService(String currentToken, String userName, int pageNumber) throws Exception {
			SearchFollowingService service = new SearchFollowingService(currentToken, userName, searchText, pageNumber);
			return service.getUsers();

		}

		@Override
		protected void handleNoUsersFound() {
			noUsersMatchCriteria.setVisibility(View.VISIBLE);
		}

		@Override
		protected void initializeUi(List<User> users) {
			UsersArrayAdapter adapter = new UsersArrayAdapter(activity, users);
			listView.setAdapter(adapter);

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
