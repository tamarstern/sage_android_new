package com.sage.application;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class GoogleAnalyticsApplication extends Application {

	private static final String PROPERTY_ID = "UA-75346379-1";

	public static int GENERAL_TRACKER = 0;

	private static TCImageLoader loader;

	public enum TrackerName {
		APP_TRACKER, GLOBAL_TRACKER, ECOMMERCE_TRACKER,
	}

	public GoogleAnalyticsApplication() {
		super();
	}

	private Tracker mTracker;

	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			mTracker = analytics.newTracker(PROPERTY_ID);
		}
		return mTracker;

	}

	synchronized public TCImageLoader getLoader() {
		if(loader == null) {
			loader = new TCImageLoader(getApplicationContext());
		}
		return loader;
	}

}