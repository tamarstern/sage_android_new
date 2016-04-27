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

            if (TextUtils.isEmpty(token) || TextUtils.isEmpty(userName)) {
                return;
            }
            for (int i = 0; i < 2; i++) {
                ArrayList<RecipeDetails> details = BackgroundServicesUtils.getNewsfeedRecipiesForPage(token, userName, i);
                NewsfeedContainer.getInstance().putRecipesForPage(i, details);
            }

        } catch (Exception e) {
            Log.e("failed fetch profile", "failed fetch profile", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }


}
