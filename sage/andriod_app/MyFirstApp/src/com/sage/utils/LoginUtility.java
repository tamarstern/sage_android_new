package com.sage.utils;

import com.sage.constants.ActivityConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LoginUtility {

	public static void saveAuthDetails(Context context, String token, String userDisplayName, String username,
			String password, String userObjectId) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ActivityConstants.AUTH_TOKEN, token);
		editor.putString(ActivityConstants.USER_DISPLAY_NAME, userDisplayName);
		editor.putString(ActivityConstants.USER_NAME, username);
		editor.putString(ActivityConstants.PASSWORD, password);
		editor.putString(ActivityConstants.USER_OBJECT_ID, userObjectId);
		editor.commit();
	}

	public static void saveUserDisplayName(Context context, String userDisplayName) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ActivityConstants.USER_DISPLAY_NAME, userDisplayName);
		editor.commit();
	}
	
	public static void savePassword(Context context, String password) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ActivityConstants.PASSWORD, password);
		editor.commit();
	}

	public static void cleanAuthenticationDetails(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ActivityConstants.AUTH_TOKEN, "");
		editor.putString(ActivityConstants.USER_DISPLAY_NAME, "");
		editor.putString(ActivityConstants.USER_NAME, "");
		editor.putString(ActivityConstants.PASSWORD, "");
		editor.putString(ActivityConstants.USER_OBJECT_ID, "");
		editor.commit();
	}

}
