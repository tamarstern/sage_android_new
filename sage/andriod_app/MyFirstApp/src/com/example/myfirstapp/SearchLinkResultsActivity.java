package com.example.myfirstapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.example.myfirstapp.GoogleResults.Result;
import com.google.gson.Gson;
import com.sage.adapters.SeachResultsAdaptor;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class SearchLinkResultsActivity extends ListActivity {



	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		handleIntent(intent);
	}

	public void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		// call detail activity for clicked entry
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
			onSearchRequested();
		} else {
			onSearchRequested();
		}
	}

	private void doSearch(String queryStr) {
		String[] query = new String[] { queryStr };
		ListView listView = getListView();
		new PerformGoogleSearch(listView).execute(query);
	}

	private class PerformGoogleSearch extends AsyncTask<String, Integer, Long> {

		private static final String UTF_8 = "UTF-8";
		private static final String GOOGLE_REST_API_SEARCH_URL = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		private GoogleResults searchResults;
		private ListView resultsView;

		public PerformGoogleSearch(ListView resultsView) {
			this.resultsView = resultsView;
		}

		protected Long doInBackground(String... queryFromUser) {
		
			String query = queryFromUser[0];
			URL url;
			try {
				url = new URL(GOOGLE_REST_API_SEARCH_URL + URLEncoder.encode(query, UTF_8));
				Reader reader = new InputStreamReader(url.openStream(), UTF_8);
				searchResults = new Gson().fromJson(reader, GoogleResults.class);

				int numberOfResults = searchResults.getResponseData().getResults().size();
				return (long) numberOfResults;
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Search is not available now, please try again later",
						Toast.LENGTH_LONG).show();
				Log.e(SearchLinkResultsActivity.class.getSimpleName(), "error while perfoming google search", e);
			}
			return null;

		}

		protected void onProgressUpdate(Integer... progress) {
			// setProgress(progress[0]);
		}

		protected void onPostExecute(Long result) {
			String[] results = getResultsUrls(searchResults.getResponseData().getResults());
			SeachResultsAdaptor adapter = new SeachResultsAdaptor(getApplicationContext(), results);

			resultsView.setAdapter(adapter);
		}

		private String[] getResultsUrls(List<Result> searchResults) {
			List<String> urls = new ArrayList<String>();
			for (Result result : searchResults) {
				urls.add(result.getUrl());
			}
			String[] urlArray = new String[urls.size()];
			String[] results = urls.toArray(urlArray);
			return results;
		}

	}

}
