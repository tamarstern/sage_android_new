package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.GetMyProfile;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by tamar.twena on 4/21/2016.
 */
public class GetProfilePageRecipesService extends IntentService {

    public GetProfilePageRecipesService() {
        super("GetProfilePageRecipesService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Log.i("startProfileBackground", "start profile backgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                initProfilePageRecipies(token, userName);
                initFollowedByCount(token, userName);
            }
            MyProfileRecipiesContainer.getInstance().removeOldRecipies();

        } catch (Exception e) {
            Log.e("failed fetch profile", "failed fetch profile", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }

    private void initFollowedByCount(String token, String userName) throws Exception {
        GetMyProfile service = new GetMyProfile(token, userName);
        JsonElement myProfile = service.getMyProfile();
        JsonObject resultJsonObject = myProfile.getAsJsonObject();

        boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

        if (requestSuccess) {
            String followedByCount = NumberFormat.getNumberInstance(Locale.US)
                    .format(resultJsonObject.get(ActivityConstants.FOLLOEWD_BY_COUNT).getAsInt());
            MyProfileRecipiesContainer.getInstance().setFollowByCountForUser(userName, followedByCount);
        }
    }

    private void initProfilePageRecipies(String token, String userName) throws Exception {
        for (int i = 0; i < 2; i++) {
            ArrayList<RecipeDetails> details = BackgroundServicesUtils.getProfilePageRecipiesForPage(token, userName, i);
            if(details != null) {
                MyProfileRecipiesContainer.getInstance().putRecipesForPage(i, details);
                MyProfileRecipiesContainer.getInstance().setMyProfileRecipesInitialized(true);
            }
        }
    }


}
