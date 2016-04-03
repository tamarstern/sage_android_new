package com.example.myfirstapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sage.adapters.SubCategoriesArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipePublished;
import com.sage.services.GetSearchMyRecipes;
import com.sage.utils.ActivityUtils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

public class SearchMyRecipesActivity extends AppCompatActivity {

	private ListView listView;

	private EditText searchEditText;

	private ImageView searchImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_my_recipes);

		listView = (ListView) findViewById(android.R.id.list);

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

		View customNav = LayoutInflater.from(this).inflate(R.layout.search_recipe_box_layout, null);
		supportActionBar.setCustomView(customNav);

		searchEditText = (EditText) customNav.findViewById(R.id.search_text);

		searchImageView = (ImageView) customNav.findViewById(R.id.search_recipe_icon);

		searchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSearchRecipies();
			}
		});

	}

	private void getSearchRecipies() {
		if (searchEditText.getEditableText() == null || TextUtils.isEmpty(searchEditText.getEditableText().toString())) {
			Toast.makeText(this, R.string.did_not_enter_search_text, Toast.LENGTH_SHORT).show();
			return;
		}
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		Editable recipeEditableToSearch = searchEditText.getText();
		String recipeTextToSearch = recipeEditableToSearch.toString();
		new SearchMyRecipesTask(this).execute(token, userName, recipeTextToSearch);

	}

	private class SearchMyRecipesTask extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;
		private ProgressDialogContainer container;

		public SearchMyRecipesTask(Activity activity) {
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement doInBackground(String... token) {

			try {
				String currentToken = token[0];
				String userName = token[1];
				String recipeText = token[2];
				GetSearchMyRecipes service = new GetSearchMyRecipes(currentToken, userName, recipeText);

				return service.getRecipies();
			} catch (Exception e) {
				container.dismissProgress();
				ActivityUtils.HandleConnectionUnsuccessfullToServer(activity);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			container.dismissProgress();
			if (result == null) {
				return;
			}
			JsonObject resultJsonObject = result.getAsJsonObject();

			boolean recipiesFound = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

			if (recipiesFound) {
				JsonElement dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
				JsonArray resultDataObject = dataElement.getAsJsonArray();

				Gson gson = new GsonBuilder().create();

				List<RecipePublished> recipes = gson.fromJson(resultDataObject,
						new TypeToken<ArrayList<RecipePublished>>() {
						}.getType());

				String[] headers = ActivityUtils.initializeRecipeHeaders(recipes);

				SubCategoriesArrayAdapter adapter = new SubCategoriesArrayAdapter(activity, recipes, headers);
				listView.setAdapter(adapter);
			}

		}

	}

}
