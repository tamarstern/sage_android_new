package com.sage.listeners;

import android.app.Activity;
import android.content.Intent;

import com.example.myfirstapp.ProfilePageActivity;
import com.sage.entities.EntityDataTransferConstants;

/**
 * Created by tamar.twena on 4/4/2016.
 */
public class ProfilePageHandler {


    private final Activity context;

    private String userDisplayName;
    private String userId;
    private String userObjectId;

    private boolean openUserProfile;

    public ProfilePageHandler(Activity context, String userDisplayName, String userId, String userObjectId,
                                    boolean openUserProfile) {
        this.context = context;

        this.userDisplayName = userDisplayName;
        this.userId = userId;
        this.userObjectId = userObjectId;
        this.openUserProfile = openUserProfile;

    }


    public void HandleOpenProfilePage() {
        Intent intent = new Intent(context, ProfilePageActivity.class)
                .putExtra(EntityDataTransferConstants.USER_DISPLAY_NAME_DATA_TRANSFER, userDisplayName)
                .putExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER, userId)
                .putExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER, userObjectId)
                .putExtra(EntityDataTransferConstants.OPEN_USER_PROFILE, openUserProfile);

        context.startActivity(intent);

    }

}
