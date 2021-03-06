package com.sage.utils;

import android.app.Activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsUtils {

	public static String ADD_LIKE_NEWSFEED_PAGE = "Add Like Newsfeed Page";
	public static String ADD_LIKE_RECIPE_PAGE = "Add Like Recipe Page";
	public static String PRESS_MOVE_RECIPE = "Press Move Recipe";
	public static String PRESS_DELETE_RECIPE = "Press Delete Recipe";
	public static String PRESS_UNFOLLOW_USER = "Press Unfollow User";
	public static String PRESS_FOLLOW_USER = "Press Follow User";
	public static String ADD_COMMENT = "Add Comment";
	public static String PUBLISH_RECIPE = "Publish Recipe";
	public static String ADD_RECIPE_TO_SUB_CATEGORY = "Add Recipe To Sub Category";
	public static String SAVE_SUB_CATEGORY = "Save Sub Category";
	public static String SAVE_CATEGORY = "Save Category";
	public static String SAVE_RECIPE = "Save Recipe Of Type ";
	public static String CLICK_ADD_PICTURE_TO_PICTURE_RECIPE = "Click Add Picture To Picture Recipe";
	public static String CLICK_ADD_PICTURE_RECIPE = "Click Add Picture Recipe";
	public static String CLICK_ADD_TEXT_RECIPE = "Click Add Text Recipe";
	public static String CLICK_ADD_LINK_RECIPE = "Click Add Link Recipe";
	public static String CLICK_ADD_NEW_RECIPE = "Click Add New Recipd";
	public static String VIEW_OTHER_USER_PROFILE = "View Other User Profile";
	public static String ENTER_TEXT_RECIPE_PAGE = "Enter Text Recipe Page";
	public static String PRESS_LOGIN_BUTTON = "Press Login Button";
	public static String PRESS_REGISTER_USER_BUTTON = "Press Register User Button";
	public static String ENTER_APP_WHEN_ALREADY_LOGGED_IN = "Enter App when already logged in";
	public static String OPEN_NEWSFEED_ACTIVITY = "Open Newsfeed Activity";
	public static String OPEN_MY_PROFILE = "Open My Profile";

	public static void sendAnalyticsTrackingEvent(Activity activity, String text) {
		Tracker t = ((com.sage.application.GoogleAnalyticsApplication) activity.getApplication()).getDefaultTracker();
		t.setScreenName(text);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

}
