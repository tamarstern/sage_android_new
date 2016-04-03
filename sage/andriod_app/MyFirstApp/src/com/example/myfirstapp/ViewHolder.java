package com.example.myfirstapp;

import android.app.Activity;
import android.webkit.WebView;

public class ViewHolder {
	/**
	 * 
	 */
	private final Activity activity;

	/**
	 * @param activity
	 */
	public ViewHolder(Activity activity) {
		this.activity = activity;
	}

	WebView webView;
}