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

/**
 * Created by tamar.twena on 4/21/2016.
 */
public class GetNewsfeedRecipesService extends IntentService {

    public GetNewsfeedRecipesService() {
        super("GetNewsfeedRecipesService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Log.i("startNewsfeedBackground", "start newsfeed backgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                fetchNewsfeedInBackground(token, userName);
                NewsfeedContainer.getInstance().removeOldRecipies();
            }

        } catch (Exception e) {
            Log.e("failed fetch newsfeed", "failed fetch newsfeed", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }

    private void fetchNewsfeedInBackground(String token, String userName) throws Exception {
        for (int i = 0; i < 3; i++) {
            Log.i("startNewsfeedBackground", "get newsfeed recipes for page : " + i);
            ArrayList<RecipeDetails> details = BackgroundServicesUtils.getNewsfeedRecipiesForPage(token, userName, i);
            Log.i("startNewsfeedBackground", "for page : " + i + " length is : " + details.size());
            NewsfeedContainer.getInstance().putRecipesForPage(i, details);
        }
    }


}
