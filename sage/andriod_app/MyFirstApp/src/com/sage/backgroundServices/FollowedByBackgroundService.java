package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.UserFollowingContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.GetFollowedByService;

import java.util.ArrayList;

/**
 * Created by tamar.twena on 4/19/2016.
 */
public class FollowedByBackgroundService extends IntentService {

    public FollowedByBackgroundService() {
        super("followiedByBacgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("followedByBackground", "start followed by backgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
            if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userId)) {
                fetchFollowedByBackground(token, userId);
            }
        } catch (Exception e) {
            Log.e("fail fetch followed by", "failed fetch followed by", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void fetchFollowedByBackground(String token, String userId) throws Exception {
        GetFollowedByService service = new GetFollowedByService(token, userId,0);
        JsonElement result = service.getUsers();
        if(result != null) {

            JsonObject resultJsonObject = result.getAsJsonObject();

            boolean foundResults = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

            if (foundResults) {
                JsonElement dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
                JsonArray resultDataObject = dataElement.getAsJsonArray();

                Gson gson = new GsonBuilder().create();

                ArrayList<User> users = gson.fromJson(resultDataObject, new TypeToken<ArrayList<User>>() {
                }.getType());

                UserFollowingContainer.getInstance().putFollowedBy(users);

                MyProfileRecipiesContainer.getInstance().setFollowByCountForUser(userId, Integer.toString(users.size()));
            }
        }
    }
}