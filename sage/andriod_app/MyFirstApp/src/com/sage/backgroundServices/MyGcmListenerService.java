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
import com.sage.entities.User;
import com.sage.utils.EntityUtils;
import com.sage.utils.ServicesUtils;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class MyGcmListenerService extends GcmListenerService {

    private enum MessageType {
        recipeUpdated,
        removeFollower,
        addFollower,
        addComment,
        addLike
    }

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.i("recieveGcmMessage", "receive new gcm message ");
        String messageType = data.getString("messageType");
        if(messageType.equals(MessageType.recipeUpdated.toString())) {
            Log.i("recipeUpdatedGcm", "receive new gcm message ");
            handleRecipeSavedMessage(from, data);
        }
        if(messageType.equals(MessageType.addFollower.toString())) {
            Log.i("followGcm", "receive new gcm message ");
            increaseFollowByCount(data);
        }
        if(messageType.equals(MessageType.removeFollower.toString())) {
            Log.i("unfollowGcm", "receive new gcm message ");
            decreaseFollowByCount(data);
        }
        if(messageType.equals(MessageType.addComment.toString())) {
            Log.i("addCommentGcm", "receive new gcm message ");
        }
        if(messageType.equals(MessageType.addLike.toString())) {
            Log.i("addLikeGcm", "receive new gcm message ");
        }
    }

    private void decreaseFollowByCount(Bundle data) {
        User user = getUserFromMessage(data);
        if(EntityUtils.isLoggedInUser(user.getUsername(), getApplicationContext())) {
            MyProfileRecipiesContainer.getInstance().decreaseFollowedByCount(user.get_id());
        }
    }

    private void increaseFollowByCount(Bundle data) {
        User user = getUserFromMessage(data);
        if(EntityUtils.isLoggedInUser(user.getUsername(), getApplicationContext())) {
            MyProfileRecipiesContainer.getInstance().increaseFollowedByCount(user.get_id());
        }
    }

    private User getUserFromMessage(Bundle data) {
        String followUserStr = data.getString("followUser");
        Gson gson = new Gson();
        JsonElement gcmMessageElement = gson.fromJson(followUserStr, JsonElement.class);
        JsonObject followUserStrElementAsJsonObject = gcmMessageElement.getAsJsonObject();
        return gson.fromJson(followUserStrElementAsJsonObject, User.class);
    }

    private void handleRecipeSavedMessage(String from, Bundle data) {
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
