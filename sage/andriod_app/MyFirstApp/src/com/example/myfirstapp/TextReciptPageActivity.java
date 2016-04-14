package com.example.myfirstapp;

//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.sage.activity.interfaces.IExitWithoutSaveListener;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.fragments.ToolbarFragment;
import com.sage.listeners.SaveRecipeHandler;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;

public class TextReciptPageActivity extends AppCompatActivity implements IExitWithoutSaveListener {

	private RecipeDetails recipeDetails;

	private RecipeCategory subCategory;

	private MenuItem publishMenuItem;

	private MenuItem unpublishMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_text_page);

		Intent i = getIntent();

		recipeDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		subCategory = (RecipeCategory) i
				.getSerializableExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER);

		initSupportActionBar();

		disableLockScreenAndTimeout();

		sendAnalyticsTrackingEvent();

	}

	@Override

	protected void onStop() {
		super.onStop();
		//GoogleAnalytics.getInstance(TextReciptPageActivity.this).reportActivityStop(this);

	}

	private void sendAnalyticsTrackingEvent() {
		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.ENTER_TEXT_RECIPE_PAGE);		
	}

	private void disableLockScreenAndTimeout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	private void initSupportActionBar() {
		Toolbar myToolbar = (Toolbar) findViewById(R.id.title_save_toolbar);
		setSupportActionBar(myToolbar);
		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			String recipeTitle = recipeDetails.getHeader();
			getSupportActionBar().setTitle(recipeTitle);
		} else {
			getSupportActionBar().setTitle(getResources().getString(R.string.add_recipe_title_toolbar));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this)) {
			getMenuInflater().inflate(R.menu.recipe_page_menu_for_logged_in_user, menu);

		} else {
			getMenuInflater().inflate(R.menu.recipe_page_menu_for_not_logged_in_user, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save) {
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
			SaveRecipeHandler handler = new SaveRecipeHandler(inflater, viewGroup, viewGroup, recipeDetails, this,
					subCategory);
			handler.HandleSaveRecipe();
			return true;
		} else {
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void onBackPressed() {
		if (EntityUtils.isRecipeChanges(this, recipeDetails)) {
			ActivityUtils.handleExitWithoutSavingPopup(this);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Fragment fragment = getFragmentManager().findFragmentById(R.id.recipe_details_panel);
		fragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onExistWithoutSave() {
		ToolbarFragment toolbarFragment = (ToolbarFragment) getFragmentManager()
				.findFragmentById(R.id.buttom_toolbar_fragment);
		if (toolbarFragment.getPagePressed() != null) {
			toolbarFragment.handlePagePressed();
		} else {
			Intent intent = new Intent(this, NewsfeedActivity.class);
			startActivity(intent);
		}
	}

}
