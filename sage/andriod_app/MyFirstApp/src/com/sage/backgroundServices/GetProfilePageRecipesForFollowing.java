package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.sage.application.NewsfeedContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by tamar.twena on 4/21/2016.
 */
public class GetProfilePageRecipesForFollowing extends IntentService {

    public GetProfilePageRecipesForFollowing() {
        super("GetProfilePageRecipesForFollowing");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String loggedInUserObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

            if (TextUtils.isEmpty(token) || TextUtils.isEmpty(loggedInUserObjectId)) {
                return;
            }
            ArrayList<RecipeDetails> firstPageRecipies = NewsfeedContainer.getInstance().getRecipesByPage(0);

            HashSet<String> processedUsers = new HashSet<String>();

            for(RecipeDetails details : firstPageRecipies) {
                String userId = details.getUserObjectId();
                if(shouldProcessId(loggedInUserObjectId, processedUsers, userId)) {
                    addProfileRecipesForUser(token, processedUsers, userId);
                }
                String ownerId = details.getOwnerObjectId();
                if(shouldProcessId(loggedInUserObjectId, processedUsers, ownerId)) {
                    addProfileRecipesForUser(token, processedUsers, ownerId);
                }
            }

        } catch (Exception e) {
            Log.e("failed fetch newsfeed", "failed fetch newsffeed for user", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }

    private void addProfileRecipesForUser(String token, HashSet<String> processedUsers, String userId) throws Exception {
        Log.e("process for user", "process for user " + userId);
        ArrayList<RecipeDetails> firstPage = BackgroundServicesUtils.getProfilePageRecipiesForPage(token, userId, 0);
        if(firstPage != null) {
            NewsfeedContainer.getInstance().addNewsfeedRecipesForUser(userId, firstPage);
            processedUsers.add(userId);
        }
    }

    private boolean shouldProcessId(String loggedInUserObjectId, HashSet<String> processedUsers, String userId) {
        return !TextUtils.isEmpty(userId) &&!isLoggedInUser(loggedInUserObjectId, userId)
                && !userAlreadyProcessesd(processedUsers, userId);
    }

    private boolean userAlreadyProcessesd(HashSet<String> processedUsers, String userId) {
        return processedUsers.contains(userId);
    }

    private boolean isLoggedInUser(String loggedInUserObjectId, String userId) {
        return userId.equals(loggedInUserObjectId);
    }


}
