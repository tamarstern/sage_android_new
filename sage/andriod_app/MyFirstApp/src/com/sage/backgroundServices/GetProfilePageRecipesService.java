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
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.GetPublishedRecipesForUser;

import java.util.ArrayList;

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
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

            if (TextUtils.isEmpty(token) || TextUtils.isEmpty(userName)) {
                return;
            }
            for (int i = 0; i < 2; i++) {
                GetPublishedRecipesForUser service = new GetPublishedRecipesForUser(token, userName, userName,
                        i);
                JsonElement recipies = service.getRecipies();

                JsonObject recipiesAsJsonObject = recipies.getAsJsonObject();

                boolean recipiesFound = recipiesAsJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

                if (recipiesFound) {
                    JsonElement dataElement = recipiesAsJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
                    JsonArray resultDataObject = dataElement.getAsJsonArray();
                    Gson gson = new GsonBuilder().create();
                    ArrayList<RecipeDetails> details = gson.fromJson(resultDataObject, new TypeToken<ArrayList<RecipeDetails>>() {
                    }.getType());
                    MyProfileRecipiesContainer.getInstance().putRecipesForPage(i, details);
                }
            }

        } catch (Exception e) {
            Log.e("failed fetch profile", "failed fetch profile", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }
}
