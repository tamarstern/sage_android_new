package com.example.myfirstapp;

import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeLinkDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AddRecipeAsLinkActivity extends AppCompatActivity {

	private static final String GOOGLE_URL = "https://www.google.com/";

	private ViewHolder holder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_link_page);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.save_link_toolbar);
		setSupportActionBar(myToolbar);
		getSupportActionBar().setTitle(getResources().getString(R.string.search_link_title));

		holder = new ViewHolder(this);

		holder.webView = (WebView) findViewById(R.id.recipe_link_search);
		holder.webView.setBackgroundResource(R.drawable.appliction_panel_style);
		holder.webView.getSettings().setJavaScriptEnabled(true);
		holder.webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		holder.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		holder.webView.setWebViewClient(new WebViewClient());
		holder.webView.loadUrl(GOOGLE_URL);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_link_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_link) {
			saveRecipeAsLink();
			return true;
		}
		if (item.getItemId() == R.id.action_cancel_save) {
			Intent intent = new Intent(getApplicationContext(), NewsfeedActivity.class);
			startActivity(intent);
			return true;
		} else {

			return super.onOptionsItemSelected(item);

		}
	}
	
	@Override
	public void onBackPressed() {
	    if (holder.webView.canGoBack()) {
	    	holder.webView.goBack();
	    } else {
	        super.onBackPressed();
	    }
	}

	private void saveRecipeAsLink() {
		String url = holder.webView.getUrl();
		Context applicationContext = getApplicationContext();
		RecipeLinkDetails recipeLinkDetails = new RecipeLinkDetails();
		recipeLinkDetails.setUrl(url);
		Intent intent = new Intent(applicationContext, LinkRecipePageActivity.class)
				.putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, recipeLinkDetails)
				.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);;
		startActivity(intent);
	}
}
