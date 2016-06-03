package com.sage.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.activities.R;

public class AddRecipeAsLinkActivity extends AppCompatActivity {

	private static final String GOOGLE_URL = "https://www.google.com/";

	private String lastUrlBeforeFail;

	private ViewHolder holder;

	private View no_internet_connection;

	private ProgressBar progressbar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_link_page);

		Toolbar myToolbar = (Toolbar) findViewById(R.id.save_link_toolbar);
		setSupportActionBar(myToolbar);
		getSupportActionBar().setTitle(getResources().getString(R.string.search_link_title));

		final WebView webView = (WebView) findViewById(R.id.recipe_link_page_content);

		progressbar = (ProgressBar)findViewById(R.id.display_link_progress);

		no_internet_connection = findViewById(R.id.no_internet_connection_panel);
		no_internet_connection.setVisibility(View.GONE);
		no_internet_connection.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				no_internet_connection.setVisibility(View.GONE);
				RefreshWebView();
			}
		});


		RefreshWebView();

	}

	private void RefreshWebView() {
		holder = new ViewHolder(this);

		holder.webView = (WebView) findViewById(R.id.recipe_link_search);
		holder.webView.setBackgroundResource(R.drawable.appliction_panel_style);
		holder.webView.getSettings().setJavaScriptEnabled(true);
		holder.webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		holder.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		holder.webView.setWebViewClient(new WebViewClient());
		progressbar.setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(this.lastUrlBeforeFail)) {
			holder.webView.loadUrl(lastUrlBeforeFail);
			lastUrlBeforeFail = null;
		} else {
			holder.webView.loadUrl(GOOGLE_URL);
		}
		holder.webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				super.onReceivedError(view, request, error);
				lastUrlBeforeFail = holder.webView.getUrl();
				holder.webView.setVisibility(View.GONE);
				holder.setFailedToLoad(true);
				progressbar.setVisibility(View.GONE);
				no_internet_connection.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (!holder.isFailedToLoad()) {
					progressbar.setVisibility(View.GONE);
					holder.webView.setVisibility(View.VISIBLE);
				}
			}
		});
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
		RecipeDetails recipeLinkDetails = new RecipeDetails();
		recipeLinkDetails.setRecipeType(RecipeType.LINK);
		recipeLinkDetails.setUrl(url);
		Intent intent = new Intent(applicationContext, LinkRecipePageActivity.class)
				.putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, recipeLinkDetails)
				.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);;
		startActivity(intent);
	}
}
