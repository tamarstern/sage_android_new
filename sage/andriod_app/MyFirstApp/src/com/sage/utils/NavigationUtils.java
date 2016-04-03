package com.sage.utils;

import com.example.myfirstapp.NewsfeedActivity;

import android.content.Context;
import android.content.Intent;

public class NavigationUtils {
	public static void openNewsfeed(Context context) {
		Intent intent = new Intent(context, NewsfeedActivity.class);
		context.startActivity(intent);
	}
}
