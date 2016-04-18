package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.utils.EntityUtils;

import java.io.Serializable;

public class DisplayLinkWebPageActivity extends AppCompatActivity {

	private RecipeDetails recipeDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_link_web_page);

		Intent i = getIntent();

		recipeDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.display_link_toolbar);
		setSupportActionBar(myToolbar);
		getSupportActionBar().setTitle(recipeDetails.getHeader());

		if (!TextUtils.isEmpty(recipeDetails.getUrl())) {

			ViewHolder holder = new ViewHolder(this);
			holder.webView = (WebView) findViewById(R.id.recipe_link_page_content);
			holder.webView.setBackgroundResource(R.drawable.appliction_panel_style);
			holder.webView.getSettings().setJavaScriptEnabled(true);
			holder.webView.getSettings().setRenderPriority(RenderPriority.HIGH);
			holder.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			holder.webView.setWebViewClient(new WebViewClient());
			holder.webView.loadUrl(recipeDetails.getUrl());
		}

		disableLockScreenAndTimeout();
	}

	private void disableLockScreenAndTimeout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String userId = recipeDetails.getUserId();

		Intent intent = getIntent();
		Serializable isLoggedInUserFromPeviousActivity = intent
				.getSerializableExtra(EntityDataTransferConstants.LOGGED_IN_USER_RECIPE);
		if (TextUtils.isEmpty(userId) && isLoggedInUserFromPeviousActivity != null) {
			boolean loggedInUser = Boolean.valueOf(isLoggedInUserFromPeviousActivity.toString());
			if (loggedInUser) {
				getMenuInflater().inflate(R.menu.display_my_recipe_link_activity_menu, menu);
			} else {
				getMenuInflater().inflate(R.menu.display_link_activity_menu, menu);
			}

		} else if (EntityUtils.isLoggedInUserRecipe(userId, this)) {
			getMenuInflater().inflate(R.menu.display_my_recipe_link_activity_menu, menu);
		} else {
			getMenuInflater().inflate(R.menu.display_link_activity_menu, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	private void saveRecipeAsLink() {
		Intent intent = new Intent(this, LinkRecipePageActivity.class)
				.putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, recipeDetails)
				.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);
		;
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_link) {
			saveRecipeAsLink();
			return true;
		} else if (item.getItemId() == R.id.action_cancel_save) {
			super.onBackPressed();
			return true;
		} else {

			return super.onOptionsItemSelected(item);

		}
	}

}
