package com.sage.application;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class GoogleAnalyticsApplication extends Application {

	private static final String PROPERTY_ID = "UA-75346379-1";

	public static int GENERAL_TRACKER = 0;

	private static TCImageLoader loader;

	private boolean backgroundServicesScheduled = false;


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

	  public void clearAllCaches() {

		  MyProfileRecipiesContainer.getInstance().clearAll();
		  NewsfeedContainer.getInstance().clearAll();
		  RecipeImageContainer.getInstance().clearAll();
		  UserCategoriesContainer.getInstance().clearAll();
		  UserFollowingContainer.getInstance().clearAll();

	  }

	public synchronized boolean isBackgroundServicesScheduled() {
		return backgroundServicesScheduled;
	}

	public synchronized void setBackgroundServicesScheduled(boolean backgroundServicesScheduled) {
		this.backgroundServicesScheduled = backgroundServicesScheduled;
	}
}