package com.sage.listeners;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.myfirstapp.ProfilePageActivity;
import com.example.myfirstapp.ProgressDialogContainer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.services.GetMyProfile;
import com.sage.services.GetProfileById;
import com.sage.utils.ActivityUtils;

import java.text.NumberFormat;
import java.util.Locale;

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String loggedInUserObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

        if(TextUtils.isEmpty(userId)) {
            new GetMyProfileTask().execute(token, loggedInUserObjectId);
        } else if(!TextUtils.isEmpty(this.userObjectId) && this.userObjectId.equals(loggedInUserObjectId) ) {
            new GetMyProfileTask().execute(token, loggedInUserObjectId);
        } else {
            new GetUserProfile(context).execute(token, loggedInUserObjectId, userObjectId);
        }

    }

    private void openProfilePageActivity(String followByCount, boolean isFollowing) {
        Intent intent = new Intent(context, ProfilePageActivity.class)
                .putExtra(EntityDataTransferConstants.USER_DISPLAY_NAME_DATA_TRANSFER, userDisplayName)
                .putExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER, userId)
                .putExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER, userObjectId)
                .putExtra(EntityDataTransferConstants.OPEN_USER_PROFILE, openUserProfile);

        if(followByCount != null) {
            intent.putExtra(EntityDataTransferConstants.FOLLOW_BY_COUNT, followByCount );
        }
        if(!openUserProfile) {
            intent.putExtra(EntityDataTransferConstants.IS_FOLLOWING, isFollowing);
        }

        context.startActivity(intent);
    }


    private class GetUserProfile extends AsyncTask<String, Void, JsonElement> {

        private Activity activity;
        private ProgressDialogContainer container;

        public GetUserProfile(Activity activity) {
            container = new ProgressDialogContainer(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
        }

        @Override
        protected JsonElement doInBackground(String... params) {

            try {
                String currentToken = params[0];
                String userName = params[1];
                String userToFollow = params[2];
                GetProfileById service = new GetProfileById(currentToken, userName, userToFollow);

                return service.getProfile();
            } catch (Exception e) {
                container.dismissProgress();
                ActivityUtils.HandleConnectionUnsuccessfullToServer(activity);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JsonElement result) {
            container.dismissProgress();
            if (result == null) {
                openProfilePageActivity(null, false);
                return;
            }

            JsonObject resultJsonObject = result.getAsJsonObject();

            boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

            if (requestSuccess) {
                boolean isFollowing = resultJsonObject.get(ActivityConstants.IS_FOLLOWING).getAsBoolean();
                openProfilePageActivity(null, isFollowing);

            }
        }

    }

    private class GetMyProfileTask extends AsyncTask<String, Void, JsonElement> {

        private ProgressDialogContainer container;

        public GetMyProfileTask() {
            container = new ProgressDialogContainer(context);
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
        }

        @Override
        protected JsonElement doInBackground(String... params) {
            try {
                String currentToken = params[0];
                String userName = params[1];
                GetMyProfile service = new GetMyProfile(currentToken, userName);

                return service.getMyProfile();
            } catch (Exception e) {
                container.dismissProgress();
                ActivityUtils.HandleConnectionUnsuccessfullToServer(context);

                return null;
            }

        }

        @Override
        protected void onPostExecute(JsonElement result) {
            container.dismissProgress();
            if(result == null) {
                openProfilePageActivity(null, false);
                return;
            }

            JsonObject resultJsonObject = result.getAsJsonObject();

            boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

            if (requestSuccess) {
                String follwedByCount = NumberFormat.getNumberInstance(Locale.US)
                        .format(resultJsonObject.get(ActivityConstants.FOLLOEWD_BY_COUNT).getAsInt());

                openProfilePageActivity(follwedByCount, false);

            }

        }

    }

}
