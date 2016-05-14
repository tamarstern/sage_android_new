package com.sage.activities;

import android.app.Activity;
import android.webkit.WebView;

public class ViewHolder {
	/**
	 * 
	 */
	private final Activity activity;

	private boolean failedToLoad;

	/**
	 * @param activity
	 */
	public ViewHolder(Activity activity) {
		this.activity = activity;
	}

	WebView webView;

	public boolean isFailedToLoad() {
		return failedToLoad;
	}

	public void setFailedToLoad(boolean failedToLoad) {
		this.failedToLoad = failedToLoad;
	}
}