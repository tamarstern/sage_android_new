package com.sage.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sage.activity.interfaces.ICategoryEditListener;
import com.sage.adapters.CategoriesArrayAdapter;
import com.sage.application.UserCategoriesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.EditCategoryPopupHandler;
import com.sage.services.GetCategoriesForUserService;
import com.sage.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ActivityCategoriesPage extends AppCompatActivity implements ICategoryEditListener {

	private ListView listView;

	private ArrayList<RecipeCategory> categories = new ArrayList<RecipeCategory>();

	private RecipeDetails recipeDetails;

	private EditCategoryPopupHandler handler;

	private TextView noCategoriesLbl;
	private RelativeLayout failedToLoadPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories_page);

		Intent intent = getIntent();
		recipeDetails = (RecipeDetails) intent
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		listView = (ListView) findViewById(android.R.id.list);
		noCategoriesLbl = (TextView) findViewById(R.id.no_categories_lbl);
		noCategoriesLbl.setVisibility(View.GONE);
		failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				fetchCategories();
			}
		});

		initSupportActionBar();

		initEditCategoryHandler();
		initCategories();

	}

	private void initCategories() {
		if(UserCategoriesContainer.getInstance().categoriesInitialized()) {
			initCategoriesFromCache();
		} else {
			fetchCategories();
		}
	}

	private void initCategoriesFromCache() {
		this.categories = UserCategoriesContainer.getInstance().getCategories();
		Collections.sort(categories);
		initCategoriesUI();
	}


	private void initSupportActionBar() {
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		if (recipeDetails == null) {
			getSupportActionBar().setTitle(getResources().getString(R.string.my_receipe_box));
		} else {
			getSupportActionBar().setTitle(getResources().getString(R.string.save_in_sub_category));
		}

	}

	private void initEditCategoryHandler() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		handler = new EditCategoryPopupHandler(inflater, view, this);
		handler.registerListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.categories_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_new_category) {
			handler.handleEditCategory(new RecipeCategory());
			return true;
		} else if (item.getItemId() == R.id.action_search_recipes) {
			Intent intent = new Intent(this, SearchMyRecipesActivity.class);
			startActivity(intent);
			return true;
		}else {
			return super.onOptionsItemSelected(item);

		}
	}

	private void fetchCategories() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

		new GetCategoriesForUserTask(this).execute(token, userName);

	}


	private void initCategoriesUI() {
		if (categories == null || categories.size() == 0) {
			noCategoriesLbl.setVisibility(View.VISIBLE);			
		} else {
			noCategoriesLbl.setVisibility(View.GONE);
			CategoriesArrayAdapter adapter = new CategoriesArrayAdapter(this, categories, recipeDetails);
			listView.setAdapter(adapter);

		}
	}
	
	@Override
	public void onBackPressed() {
		boolean isNewRecipeUnsaved = getIntent().getSerializableExtra(EntityDataTransferConstants.NEW_RECIPE_UNSAVED) != null;
		if(recipeDetails != null) {
			ActivityUtils.openRecipeActivity(recipeDetails, this);
		} else if(isNewRecipeUnsaved) {
			Intent intent = new Intent(this, NewsfeedActivity.class);
			startActivity(intent);
		}  else {
			super.onBackPressed();
		}	
	}


	@Override
	public void onSaveCategory(RecipeCategory category) {
		UserCategoriesContainer.getInstance().putCategory(category);

		initCategoriesFromCache();
		noCategoriesLbl.setVisibility(View.GONE);
	}

	private class GetCategoriesForUserTask extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;
		private ProgressDialogContainer container;

		public GetCategoriesForUserTask(Activity activity) {
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

				GetCategoriesForUserService service = new GetCategoriesForUserService(currentToken, userName);

				return service.getCategories();
			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			container.dismissProgress();
			if (result == null) {
				failedToLoadPanel.setVisibility(View.VISIBLE);
				return;
			}
			JsonArray resultJsonObject = result.getAsJsonArray();

			Gson gson = new GsonBuilder().create();

			categories = gson.fromJson(resultJsonObject, new TypeToken<ArrayList<RecipeCategory>>() {
			}.getType());
			UserCategoriesContainer.getInstance().putCategories(new HashSet<RecipeCategory>(categories));
			UserCategoriesContainer.getInstance().setCategoriesInitialized(true);
			initCategoriesUI();

		}
	}

	@Override
	public void onDeleteCategory(RecipeCategory category) {
		UserCategoriesContainer.getInstance().deleteCategory(category);
		initCategoriesFromCache();
		
	}

}
