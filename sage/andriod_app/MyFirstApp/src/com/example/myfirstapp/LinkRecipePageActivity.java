package com.example.myfirstapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.activity.interfaces.IExitWithoutSaveListener;
import com.sage.activity.interfaces.IInitLinkDetailsListener;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeLinkDetails;
import com.sage.fragments.ToolbarFragment;
import com.sage.listeners.SaveRecipeHandler;
import com.sage.tasks.GetRecipeUrlDetailsTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImagesInitializer;
import com.sage.utils.RecipeDetailsBinder;
import com.sage.utils.RecipeOwnerContext;

import java.util.HashSet;
import java.util.Set;

public class LinkRecipePageActivity extends AppCompatActivity implements IExitWithoutSaveListener {

	private RecipeLinkDetails recipeDetails;

	private RecipeCategory category;

	private EditText comments;

	private TextView recipeLinkContent;

	final Activity activity = this;

	private MenuItem publishMenuItem;

	private MenuItem unpublishMenuItem;

	private TextView linkHeader;

	private ImageView linkImage;

	private ProgressBar getLinkDetailsProgress;

	private Set<IInitLinkDetailsListener> listeners = new HashSet<IInitLinkDetailsListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_link_recipe_page);

		Fragment fragment = (Fragment) getFragmentManager().findFragmentById(R.id.recipe_title_panel);
		if (fragment instanceof IInitLinkDetailsListener) {
			listeners.add((IInitLinkDetailsListener) fragment);
		}

		Fragment ownerfragment = (Fragment) getFragmentManager().findFragmentById(R.id.recipe_owner_panel);
		if (ownerfragment instanceof IInitLinkDetailsListener) {
			listeners.add((IInitLinkDetailsListener) ownerfragment);
		}

		Intent i = getIntent();

		recipeDetails = (RecipeLinkDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		category = (RecipeCategory) i
				.getSerializableExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER);

		initSupportActionBar();

		initLinkUi();

		this.comments = (EditText) findViewById(R.id.recipe_comments_content);

		LinearLayout commentsPanel = (LinearLayout) findViewById(R.id.receipt_comments_panel);

		if (!EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this.comments.getContext())) {
			commentsPanel.setVisibility(View.GONE);
		}
		RecipeDetailsBinder.bindTextBoxToComments(comments, recipeDetails);

		this.recipeLinkContent = (TextView) findViewById(R.id.recipe_link_content);

		String recipeUrl = recipeDetails.getUrl();
		if (!TextUtils.isEmpty(recipeUrl)) {
			recipeLinkContent.setText(recipeUrl);
			recipeLinkContent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openDisplayLinkActivity();

				}
			});
			Object[] params = ActivityUtils.generateServiceParamObject(this, recipeDetails.getUrl());
			new GetRecipeUrlDetails(recipeDetails, this).execute(params);

		}

		initPreparationComments();

		disableLockScreenAndTimeout();

	}

	private void disableLockScreenAndTimeout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	private void initLinkUi() {
		getLinkDetailsProgress = (ProgressBar) findViewById(R.id.get_recipe_details_progress);
		linkHeader = (TextView) findViewById(R.id.recipe_link_title);
		linkImage = (ImageView) findViewById(R.id.recipe_link_main_picture);

		getLinkDetailsProgress.setVisibility(View.VISIBLE);
		linkHeader.setVisibility(View.GONE);
		linkImage.setVisibility(View.GONE);

		linkHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openDisplayLinkActivity();

			}
		});

		linkImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openDisplayLinkActivity();

			}
		});
	}

	private void initPreparationComments() {
		String recipeComments = recipeDetails.getPreparationComments();
		if (!TextUtils.isEmpty(recipeComments)) {
			comments.setText(recipeComments);
			recipeDetails.setRecipeChanges(false);
		}
		comments.setGravity(Gravity.LEFT);
		comments.setSingleLine(false);
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
					category);
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

	private void initLinkRecipeUi(RecipeLinkDetails linkDetails) {

		String linkTitle = linkDetails.getLinkTitle();
		if (!TextUtils.isEmpty(linkTitle)) {
			linkHeader.setText(linkTitle);
		} else {
			linkHeader.setText(linkDetails.getHeader());
		}

		String linkImageUrl = linkDetails.getLinkImageUrl();
		if (!TextUtils.isEmpty(linkImageUrl)) {
			ImagesInitializer.initImage(this, linkImage, linkImageUrl);
		}

		getLinkDetailsProgress.setVisibility(View.GONE);

		linkHeader.setVisibility(View.VISIBLE);
		linkImage.setVisibility(View.VISIBLE);
		for (IInitLinkDetailsListener listener : this.listeners) {
			listener.onInitLinkDetails();
		}

	}

	private void openDisplayLinkActivity() {
		ActivityUtils.openDisplayLinkActivity(this, recipeDetails,
				EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this));
	}

	private class GetRecipeUrlDetails extends GetRecipeUrlDetailsTask {

		private RecipeLinkDetails linkDetails;

		public GetRecipeUrlDetails(RecipeLinkDetails linkDetails, Activity activity) {
			super(activity);
			this.linkDetails = linkDetails;

		}

		@Override
		protected void handleSuccess(JsonObject resultJsonObject) {
			JsonElement urlTitleElement = resultJsonObject.get(ActivityConstants.URL_TITLE_ELEMENT_NAME);
			if (urlTitleElement != null) {
				String urlTitle = urlTitleElement.getAsString();
				linkDetails.setLinkTitle(urlTitle);
			}

			JsonElement urlImageElement = resultJsonObject.get(ActivityConstants.URL_IMAGE_ELEMENT_NAME);
			if (urlImageElement != null) {
				String urlImage = urlImageElement.getAsString();
				linkDetails.setLinkImageUrl(urlImage);

			}

			String siteName = null;
			JsonElement urlSiteJson = resultJsonObject.get(ActivityConstants.URL_SITE_ELEMENT_NAME);
			if (urlSiteJson != null) {
				siteName = urlSiteJson.getAsString();
				RecipeOwnerContext.setOwnerForUrl(linkDetails.getUrl(), siteName);
				linkDetails.setLinkSiteName(siteName);
			}

			initLinkRecipeUi(linkDetails);

		}

	}

}
