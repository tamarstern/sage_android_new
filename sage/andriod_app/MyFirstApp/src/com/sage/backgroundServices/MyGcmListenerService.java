package com.sage.backgroundServices;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.NewsfeedContainer;
import com.sage.entities.RecipeDetails;
import com.sage.utils.EntityUtils;
import com.sage.utils.ServicesUtils;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("recieveGcmMessage", "receive new gcm message ");
        String message = data.getString("publishedRecipe");
        Log.i(TAG, "From: " + from);
        Log.i(TAG, "Message: " + message);

        Gson gson = new Gson();
        JsonElement gcmMessageElement = gson.fromJson(message, JsonElement.class);
        JsonObject gcmMessageElementAsJsonObject = gcmMessageElement.getAsJsonObject();
        RecipeDetails detailsFromResponse = ServicesUtils.createRecipeDetailsFromResponse(gson, gcmMessageElementAsJsonObject);
        NewsfeedContainer.getInstance().updateRecipeInNewsfeed(detailsFromResponse);
        if(EntityUtils.isLoggedInUserRecipe(detailsFromResponse.getUserId(), getApplicationContext())) {
            MyProfileRecipiesContainer.getInstance().addRecipe(detailsFromResponse);
        } else {
            NewsfeedContainer.getInstance().addRecipeForUser(detailsFromResponse.getUserObjectId(), detailsFromResponse);
        }


    }
}
