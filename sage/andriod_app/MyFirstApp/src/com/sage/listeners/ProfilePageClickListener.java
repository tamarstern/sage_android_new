package com.sage.listeners;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class ProfilePageClickListener implements OnClickListener {

	/**
	 * 
	 */
	private ProfilePageHandler handler;
	public ProfilePageClickListener(Activity context, String userDisplayName, String userId, String userObjectId,
			boolean openUserProfile) {
		handler = new ProfilePageHandler(context, userDisplayName, userId,userObjectId,openUserProfile, false);
	}

	@Override
	public void onClick(View v) {
		handler.HandleOpenProfilePage();
	}

}