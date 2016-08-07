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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sage.adapters.NewsfeedArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.GetSearchRecipes;
import com.sage.tasks.GetRecipiesActivity;
import com.sage.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchActivity extends AppCompatActivity {

	ListView listView;

	private ImageView searchImageView;

	private AppCompatTextView switchToUserSearch;

	private EditText searchEditText;

	private NewsfeedArrayAdapter adapter;
	private ProgressBar progressBar;
	private boolean afterStop;

	private RelativeLayout failedToLoadPanel;


	private int pageNumber = 0;
	private int preLast = 0;
	private boolean shouldIncreasePage = true;
	private volatile boolean loadingMore = false;

	private TextView noRecipesMatchSearchCriteria;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		noRecipesMatchSearchCriteria = (TextView)findViewById(R.id.no_recipes_found_matching_search_criteria);
		noRecipesMatchSearchCriteria.setVisibility(View.GONE);
		failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				getSearchRecipies();
			}
		});


		View footer = getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
		progressBar = (ProgressBar) footer.findViewById(R.id.get_recipies_progress);

		progressBar.setVisibility(View.GONE);
		listView = (ListView) findViewById(android.R.id.list);
		listView.addFooterView(footer);
		initListView();
		initializeActionBar();

		Intent intent = getIntent();
		String searchTextTransfered = (String) intent
				.getSerializableExtra(ActivityConstants.SEARCH_TEXT_TRANSFERED);

		if(!TextUtils.isEmpty(searchTextTransfered)) {
			searchEditText.setText(searchTextTransfered);
			OnClickSearchButton();
		}

	}

	@Override
	protected void onStop() {
		this.afterStop = true;
		super.onStop();
	}

	private void initListView() {
		if (listView.getAdapter() == null) {
			initListAdaptor();
		}

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				if (searchEditText == null || isSearchTextEmpty()) {
					return;
				}
				if (afterStop) {
					afterStop = false;
					return;
				}

				if (loadingMore) {
					return;
				}
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if ((lastInScreen == totalItemCount)) {
					if (preLast != lastInScreen && !shouldIncreasePage) {
						preLast = lastInScreen;
						getSearchRecipies();
					}

				}
			}
		});
	}

	private void initListAdaptor() {
		adapter = new NewsfeedArrayAdapter(this, new ArrayList<RecipeDetails>());
		listView.setAdapter(adapter);
	}


	private void initializeActionBar() {

		final Activity activity = this;
		Toolbar myToolbar = (Toolbar) findViewById(R.id.search_toolbar);
		setSupportActionBar(myToolbar);

		android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
		supportActionBar.setDisplayShowTitleEnabled(false);
		supportActionBar.setDisplayUseLogoEnabled(false);
		supportActionBar.setDisplayHomeAsUpEnabled(false);
		supportActionBar.setDisplayShowCustomEnabled(true);
		supportActionBar.setDisplayShowHomeEnabled(false);

		View customNav = LayoutInflater.from(this).inflate(R.layout.search_recipes_layout, null);
		supportActionBar.setCustomView(customNav);
		Toolbar parent = (Toolbar)customNav.getParent();
		parent.setContentInsetsAbsolute(0,0);
		//parent.setContentInsetsRelative(0,0);

		searchEditText = (EditText) customNav.findViewById(R.id.search_text);

		searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					OnClickSearchButton();
					ActivityUtils.hideSoftKeyboard(activity);
					return true;
				}
				return false;
			}
		});

		searchImageView = (ImageView) customNav.findViewById(R.id.search_recipe_icon);

		searchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				OnClickSearchButton();
				ActivityUtils.hideSoftKeyboard(activity);
			}
		});

		switchToUserSearch = (AppCompatTextView) customNav.findViewById(R.id.search_users);

		switchToUserSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, SearchUserActivity.class);
				Editable editableText = searchEditText.getEditableText();
				if(editableText != null) {
					intent.putExtra(ActivityConstants.SEARCH_TEXT_TRANSFERED, editableText.toString());
				}
				startActivity(intent);

			}
		});

	}

	private void OnClickSearchButton() {
		pageNumber = 0;
		noRecipesMatchSearchCriteria.setVisibility(View.GONE);
		initListAdaptor();
		getSearchRecipies();
	}

	private void getSearchRecipies() {
		if (searchEditText == null) {
			return;
		}
		if (isSearchTextEmpty()) {
			Toast.makeText(this, R.string.did_not_enter_search_text, Toast.LENGTH_SHORT).show();
			return;
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		new GetRecipiesForSearch<RecipeDetails>(this).execute(token, userName, pageNumber);

	}

	private boolean isSearchTextEmpty() {
		return searchEditText.getEditableText() == null
				|| TextUtils.isEmpty(searchEditText.getEditableText().toString());
	}

	private class GetRecipiesForSearch<T extends RecipeDetails> extends GetRecipiesActivity {
	
		private Activity activity;
		public GetRecipiesForSearch(Activity activity) {
			super(new TypeToken<ArrayList<RecipeDetails>>() {
			}.getType(), activity);

		}

		@Override
		protected void performCustomActionsOnPreExecute() {
			loadingMore = true;
			shouldIncreasePage = true;
			if(pageNumber == 0) {
				super.performCustomActionsOnPreExecute();
			}else if (listView.getAdapter().getCount()-1 > 0) {
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
			}else if (listView.getAdapter().getCount()-1 > 0) {
				progressBar.setVisibility(View.GONE);
			} else {
				super.performCustomActionsOnException();
			}
		}

		@Override
		protected void performCustomActionsOnPostExecute() {
			loadingMore = false;
			if(pageNumber == 0) {
				super.performCustomActionsOnPostExecute();
			}else if (listView.getAdapter().getCount()-1 > 0) {
				progressBar.setVisibility(View.GONE);
			} else  {
				super.performCustomActionsOnPostExecute();
			}
		}

		@Override
		protected JsonElement doInBackground(Object... token) {
			return super.doInBackground(token);
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			super.onPostExecute(result);
			if (details != null) {
				Iterator iterator = details.iterator();
				while (iterator.hasNext()) {
					adapter.add((RecipeDetails) iterator.next());
				}
				adapter.notifyDataSetChanged();
				if(shouldIncreasePage) {
					pageNumber += 1;
					shouldIncreasePage = false;
				}				
				loadingMore = false;

			}

		}

		@Override
		protected void handleNoRecipesFound() {
			if(pageNumber == 0) {
				noRecipesMatchSearchCriteria.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected JsonElement CreateAndExecuteService(String currentToken, String userName, int pageNumber)
				throws Exception {
			Editable recipeEditableToSearch = searchEditText.getText();
			String recipeTextToSearch = recipeEditableToSearch == null ? "" : recipeEditableToSearch.toString();
			GetSearchRecipes service = new GetSearchRecipes(currentToken, userName, recipeTextToSearch, pageNumber);
			return service.getRecipies();
		}

	}
}
