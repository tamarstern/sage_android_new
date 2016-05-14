package com.sage.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;

import com.sage.activities.R;


public class SeachResultsAdaptor extends ArrayAdapter<String> {
	private final Context context;
	private String[] results;

	public SeachResultsAdaptor(Context context, String[] results) {
		super(context, -1, results);
		this.context = context;
		this.results = results;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {

			// res = getLayoutInflater().inflate(R.layout.item_composer, null);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.search_results_row_layout, parent, false);

			ViewHolder holder = new ViewHolder();

			holder.webView = (WebView) rowView.findViewById(R.id.search_result_web_page);
			holder.webView.getSettings().setJavaScriptEnabled(true);

			holder.webView.setWebViewClient(new WebViewClient());

			rowView.setTag(holder);

		}

		ViewHolder holder = (ViewHolder) rowView.getTag();

		holder.webView.loadUrl(results[position]);// loading webview with URL

		return rowView;

	}

	public class ViewHolder {
		WebView webView;
	}
}
