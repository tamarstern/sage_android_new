package com.sage.listeners;

import com.example.myfirstapp.ProfilePageActivity;
import com.sage.entities.EntityDataTransferConstants;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ProfilePageClickListener implements OnClickListener {

	/**
	 * 
	 */
	private final Context context;

	private String userDisplayName;
	private String userId;
	private String userObjectId;

	private boolean openUserProfile;

	public ProfilePageClickListener(Context context, String userDisplayName, String userId, String userObjectId,
			boolean openUserProfile) {
		this.context = context;
		
		this.userDisplayName = userDisplayName;
		this.userId = userId;
		this.userObjectId = userObjectId;
		this.openUserProfile = openUserProfile;

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(context, ProfilePageActivity.class)
				.putExtra(EntityDataTransferConstants.USER_DISPLAY_NAME_DATA_TRANSFER, userDisplayName)
				.putExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER, userId)
				.putExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER, userObjectId)
				.putExtra(EntityDataTransferConstants.OPEN_USER_PROFILE, openUserProfile);

		context.startActivity(intent);

	}

}