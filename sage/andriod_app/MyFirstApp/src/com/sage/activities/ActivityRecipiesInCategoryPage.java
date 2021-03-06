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
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sage.adapters.RecipesForCategoriesArrayAdapter;
import com.sage.application.UserCategoriesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.AddRecipePopupHandler;
import com.sage.services.GetRecipiesForCategoryService;
import com.sage.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ActivityRecipiesInCategoryPage extends AppCompatActivity {

	private RecipeCategory category;

	private ListView listView;

	private LayoutInflater inflater;

	private ViewGroup viewGroup;

	private RelativeLayout failedToLoadPanel;

	private TextView noRecipesInCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_in_category_page);

		listView = (ListView) findViewById(android.R.id.list);


		noRecipesInCategory = (TextView)findViewById(R.id.no_recipes_in_categories_lbl);
		noRecipesInCategory.setVisibility(View.GONE);
		failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
		failedToLoadPanel.setVisibility(View.GONE);
		failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				failedToLoadPanel.setVisibility(View.GONE);
				fetchRecipes();
			}
		});


		Intent i = getIntent();

		category = (RecipeCategory) i
				.getSerializableExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER);

		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		viewGroup = (ViewGroup) getWindow().getDecorView();

		initSupportActionBar();

		fetchRecipes();

	}

	private void initSupportActionBar() {
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);
		getSupportActionBar().setTitle(category.getHeader());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sub_categories_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_new_recipe) {
			AddRecipePopupHandler popupHandler = new AddRecipePopupHandler(inflater, viewGroup, viewGroup, this,
					category);
			popupHandler.handleAddRecipe();
			return true;
		} else if (item.getItemId() == R.id.action_search_recipes) {
			Intent intent = new Intent(this, SearchMyRecipesActivity.class);
			startActivity(intent);
			return true;
		} else {

			return super.onOptionsItemSelected(item);

		}
	}

	private void initRecipesPerCategoryUi(HashSet<RecipeDetails> recipesForCategory) {
		if(recipesForCategory.size() == 0) {
			noRecipesInCategory.setVisibility(View.VISIBLE);
		} else {
			noRecipesInCategory.setVisibility(View.GONE);
			ArrayList<RecipeDetails> recipes = new ArrayList<>(recipesForCategory);
			Collections.sort(recipes);
			initAdapter(recipes);
			return;
		}
	}

	private void fetchRecipes() {
		if(UserCategoriesContainer.getInstance().hasRecipesForCategory(this.category)) {
			HashSet<RecipeDetails> recipesForCategory = UserCategoriesContainer.getInstance().getRecipesForCategory(this.category);
			initRecipesPerCategoryUi(recipesForCategory);
			return;
		}

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
		String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
		new GetRecipesForCategoryTask(this).execute(token, userName);

	}

	private void initAdapter(ArrayList<RecipeDetails> recipes) {
		Collections.sort(recipes);
		RecipesForCategoriesArrayAdapter adapter = new RecipesForCategoriesArrayAdapter(this, recipes);
		listView.setAdapter(adapter);
	}


	private class GetRecipesForCategoryTask extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;

		private ProgressDialogContainer container;

		public GetRecipesForCategoryTask(Activity activity) {
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

				GetRecipiesForCategoryService service = new GetRecipiesForCategoryService(currentToken, userName,
						category);

				return service.getRecipes();
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
			JsonObject resultJsonObject = result.getAsJsonObject();

			boolean recipiesFound = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			ArrayList<RecipeDetails> recipes = new ArrayList<RecipeDetails>();
			if (recipiesFound) {
				JsonElement dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
				JsonArray resultDataObject = dataElement.getAsJsonArray();


				Gson gson = new GsonBuilder().create();

				recipes = gson.fromJson(resultDataObject, new TypeToken<ArrayList<RecipeDetails>>() {}.getType());
			}
			HashSet<RecipeDetails> recipesForCategory = new HashSet<RecipeDetails>(recipes);
			UserCategoriesContainer.getInstance().putRecipesForCategory(category, recipesForCategory);
			initRecipesPerCategoryUi(recipesForCategory);
		}


	}
}
