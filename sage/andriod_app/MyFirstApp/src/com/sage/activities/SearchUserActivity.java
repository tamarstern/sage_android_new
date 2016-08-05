package com.sage.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
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
import com.sage.services.SearchUsersService;
import com.sage.tasks.BaseFetchUsersTask;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

	private ListView listView;

	private EditText searchEditText;

	private ImageView searchImageView;

	private AppCompatTextView switchToSearchRecipe;


	private int pageNumber;

	private TextView noUsersFoundThatMatchCriteria;

	private RelativeLayout failedToLoadPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_user);

		listView = (ListView) findViewById(android.R.id.list);

		noUsersFoundThatMatchCriteria = (TextView)findViewById(R.id.no_users_found_matching_search_criteria);
		noUsersFoundThatMatchCriteria.setVisibility(View.GONE);
		failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				noUsersFoundThatMatchCriteria.setVisibility(View.GONE);
				fetchUsers();
			}
		});


		initSupportActionBar();

		Intent intent = getIntent();
		String searchTextTransfered = (String) intent
				.getSerializableExtra(ActivityConstants.SEARCH_TEXT_TRANSFERED);

		if(!TextUtils.isEmpty(searchTextTransfered)) {
			searchEditText.setText(searchTextTransfered);
			failedToLoadPanel.setVisibility(View.GONE);
			fetchUsers();
		}


	}


	private void initSupportActionBar() {
		final Activity activity = this;
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
		supportActionBar.setDisplayShowTitleEnabled(false);
		supportActionBar.setDisplayUseLogoEnabled(false);
		supportActionBar.setDisplayHomeAsUpEnabled(false);
		supportActionBar.setDisplayShowCustomEnabled(true);
		supportActionBar.setDisplayShowHomeEnabled(false);


		View customNav = LayoutInflater.from(this).inflate(R.layout.search_users_layout, null);
		supportActionBar.setCustomView(customNav);
		Toolbar parent = (Toolbar)customNav.getParent();
		parent.setContentInsetsAbsolute(0,0);

		searchEditText = (EditText) customNav.findViewById(R.id.search_text);

		searchImageView = (ImageView) customNav.findViewById(R.id.search_user_icon);

		searchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				fetchUsers();
			}
		});

		switchToSearchRecipe = (AppCompatTextView) customNav.findViewById(R.id.search_recipes);

		switchToSearchRecipe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, SearchActivity.class);
				Editable editableText = searchEditText.getEditableText();
				if(editableText != null) {
					intent.putExtra(ActivityConstants.SEARCH_TEXT_TRANSFERED, editableText.toString());
				}
				startActivity(intent);
			}
		});

	}

	private void fetchUsers() {
		if (searchEditText.getEditableText() == null
				|| TextUtils.isEmpty(searchEditText.getEditableText().toString())) {
			Toast.makeText(this, R.string.did_not_enter_search_text, Toast.LENGTH_SHORT).show();
			return;
		}

		initListAdapter(new ArrayList<User>());

		noUsersFoundThatMatchCriteria.setVisibility(View.GONE);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

		Editable editableText = searchEditText.getEditableText();

		String searchText = editableText.toString();

		String[] params = new String[] { token, searchText, Integer.toString(pageNumber) };

		new SearchUsersTask(this).execute(params);

	}

	private void initListAdapter(List<User> users) {
		UsersArrayAdapter adapter = new UsersArrayAdapter(this, users);
		listView.setAdapter(adapter);
	}

	private class SearchUsersTask extends BaseFetchUsersTask {

		private Activity activity;
		private ProgressDialogContainer container;

		public SearchUsersTask(Activity activity) {
			super(activity);
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement createAndExecuteService(String currentToken, String textToSearch, int pageNumber)
				throws Exception {
			SearchUsersService service = new SearchUsersService(currentToken, textToSearch, pageNumber);
			return service.getUsers();

		}

		@Override
		protected void handleNoUsersFound() {
			noUsersFoundThatMatchCriteria.setVisibility(View.VISIBLE);
		}

		@Override
		protected void initializeUi(List<User> users) {
			initListAdapter(users);


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
