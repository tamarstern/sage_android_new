package com.sage.backgroundServices;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.activities.LinkRecipePageActivity;
import com.sage.activities.PictureRecipePageActivity;
import com.sage.activities.ProfilePageActivity;
import com.sage.activities.R;
import com.sage.activities.TextReciptPageActivity;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.NewsfeedContainer;
import com.sage.application.UserFollowingContainer;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.User;
import com.sage.utils.ActivityUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ServicesUtils;

import java.text.MessageFormat;
import java.util.Random;

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
        if (messageType.equals(MessageType.recipeUpdated.toString())) {
            Log.i("recipeUpdatedGcm", "receive new gcm message ");
            handleRecipeSavedMessage(from, data);
        }
        if (messageType.equals(MessageType.addFollower.toString())) {
            Log.i("followGcm", "receive new gcm message ");
            handleFollowMessage(data);
        }
        if (messageType.equals(MessageType.removeFollower.toString())) {
            Log.i("unfollowGcm", "receive new gcm message ");
            decreaseFollowByCount(data);
        }
        if (messageType.equals(MessageType.addComment.toString())) {
            Log.i("addCommentGcm", "receive new gcm message ");
            String recipe = data.getString("recipe");
            RecipeDetails details = getRecipeDetailsFromMessage(recipe);
            String userStr = data.getString("userWhoAddComment");
            User user = getUserFromMessage(userStr);
            if(EntityUtils.isLoggedInUser(user.getUsername(), this)) {
                return;
            }
            if(user != null && recipe != null) {
                sendCommentNotification(details, user);
            }
        }
        if (messageType.equals(MessageType.addLike.toString())) {
            Log.i("addLikeGcm", "receive new gcm message ");
            String recipe = data.getString("recipe");
            RecipeDetails details = getRecipeDetailsFromMessage(recipe);
            String userStr = data.getString("userWhoAddLike");
            User user = getUserFromMessage(userStr);
            if(EntityUtils.isLoggedInUser(user.getUsername(), this)) {
                return;
            }
            if(user != null && recipe != null) {
                sendLikeNotification(details, user);
            }
        }
    }

    private void sendCommentNotification(RecipeDetails details, User user) {

        String notificationDescription = getApplicationContext().getResources().getString(R.string.comment_push_notification_message);
        String notificationDescriptionToShow = MessageFormat.format(notificationDescription, user.getUserDisplayName());
        buildNotificationForRecipePage(details, notificationDescriptionToShow);
    }

    private void sendLikeNotification(RecipeDetails recipe, User user) {
        String notificationDescription = getApplicationContext().getResources().getString(R.string.like_push_notification_message);
        String notificationDescriptionToShow = MessageFormat.format(notificationDescription, user.getUserDisplayName());
        buildNotificationForRecipePage(recipe, notificationDescriptionToShow);
    }

    private void buildNotificationForProfilePage(User user, String notificationDescriptionToShow) {
        String notificationTitle = getApplicationContext().getResources().getString(R.string.push_notifications_title);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.sage_dashboard_icon)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationDescriptionToShow).
                        setAutoCancel(true);

        Intent resultIntent = getProfilePageIntent(user);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(ProfilePageActivity.class);

        drawNotification(mBuilder, resultIntent, stackBuilder);

    }

    private void buildNotificationForRecipePage(RecipeDetails recipe, String notificationDescriptionToShow) {

        String notificationTitle = getApplicationContext().getResources().getString(R.string.push_notifications_title);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.sage_dashboard_icon)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationDescriptionToShow).setAutoCancel(true);

        Intent resultIntent = ActivityUtils.getRecipeIntent(recipe, this);

        drawRecipeNotification(recipe, mBuilder, resultIntent);
    }

    private void drawRecipeNotification(RecipeDetails recipe, NotificationCompat.Builder mBuilder, Intent resultIntent) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        addActivityClassToStackBuilder(recipe, stackBuilder);

        drawNotification(mBuilder, resultIntent, stackBuilder);
    }

    private void drawNotification(NotificationCompat.Builder mBuilder, Intent resultIntent, TaskStackBuilder stackBuilder) {
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int messageId = new Random().nextInt(10000);
        mNotificationManager.notify(messageId, mBuilder.build());
    }

    private void addActivityClassToStackBuilder(RecipeDetails recipe, TaskStackBuilder stackBuilder) {
        switch (recipe.getRecipeType()) {
            case PICTURE:
                stackBuilder.addParentStack(PictureRecipePageActivity.class);
                break;
            case LINK:
                stackBuilder.addParentStack(LinkRecipePageActivity.class);
                break;
            default:
                stackBuilder.addParentStack(TextReciptPageActivity.class);
                break;
        }
    }

    private void decreaseFollowByCount(Bundle data) {
        String followUserStr = data.getString("followedBy");
        if(TextUtils.isEmpty(followUserStr)) {
            return;
        }
        User user = getUserFromMessage(followUserStr);
        if (EntityUtils.isLoggedInUser(user.getUsername(), getApplicationContext())) {
            MyProfileRecipiesContainer.getInstance().decreaseFollowedByCount(user.get_id());
        }
    }

    private void handleFollowMessage(Bundle data) {
        increaseFollowByCount(data);
        drawNotificationMessage(data);
    }

    private void drawNotificationMessage(Bundle data) {
        String baseUserStr = data.getString("baseUser");
        if(TextUtils.isEmpty(baseUserStr)) {
            return;
        }
        User baseUser = getUserFromMessage(baseUserStr);
        if(baseUser == null) {
            return;
        }
        String pushFollowDescription = getApplicationContext().getResources().getString(R.string.follow_push_notification_message);
        String pushFollowDescriptionToShow = MessageFormat.format(pushFollowDescription, baseUser.getUserDisplayName());
        buildNotificationForProfilePage(baseUser, pushFollowDescriptionToShow);
    }

    private void increaseFollowByCount(Bundle data) {
        String followUserStr = data.getString("followUser");
        if(TextUtils.isEmpty(followUserStr)) {
            return;
        }
        User user = getUserFromMessage(followUserStr);
        if(user == null) {
            return;
        }
        if (EntityUtils.isLoggedInUser(user.getUsername(), getApplicationContext())) {
            MyProfileRecipiesContainer.getInstance().increaseFollowedByCount(user.get_id());
        }
    }

    private Intent getProfilePageIntent(User user) {
        Intent intent = new Intent(this, ProfilePageActivity.class)
                .putExtra(EntityDataTransferConstants.USER_DISPLAY_NAME_DATA_TRANSFER, user.getUserDisplayName())
                .putExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER, user.getUsername())
                .putExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER, user.get_id())
                .putExtra(EntityDataTransferConstants.OPEN_USER_PROFILE, false);
        boolean isFollowing = UserFollowingContainer.getInstance().isFollowing(user);
        intent.putExtra(EntityDataTransferConstants.IS_FOLLOWING, isFollowing);
        return intent;
    }

    private User getUserFromMessage(String followUserStr) {
        Gson gson = new Gson();
        JsonElement gcmMessageElement = gson.fromJson(followUserStr, JsonElement.class);
        JsonObject followUserStrElementAsJsonObject = gcmMessageElement.getAsJsonObject();
        return gson.fromJson(followUserStrElementAsJsonObject, User.class);
    }

    private void handleRecipeSavedMessage(String from, Bundle data) {
        Log.i(TAG, "From: " + from);
        String recipeMessage = data.getString("publishedRecipe");
        Log.i(TAG, "Message: " + recipeMessage);
        RecipeDetails detailsFromResponse = getRecipeDetailsFromMessage(recipeMessage);
        updateSavedRecipeInCache(detailsFromResponse);
        String userMessage = data.getString("userWhoSharedRecipe");
        User user = getUserFromMessage(userMessage);
        if(user == null || TextUtils.isEmpty(user.getUserDisplayName())) {
            return;
        }
        String pushFollowDescription = getApplicationContext().getResources().getString(R.string.recipe_updated_push_notification_message);
        String pushFollowDescriptionToShow = MessageFormat.format(pushFollowDescription, user.getUserDisplayName());
        buildNotificationForRecipePage(detailsFromResponse,pushFollowDescriptionToShow);
    }

    private void updateSavedRecipeInCache(RecipeDetails detailsFromResponse) {
        NewsfeedContainer.getInstance().updateRecipeInNewsfeed(detailsFromResponse);
        if (EntityUtils.isLoggedInUserRecipe(detailsFromResponse.getUserId(), getApplicationContext())) {
            MyProfileRecipiesContainer.getInstance().addRecipe(detailsFromResponse);
        } else {
            NewsfeedContainer.getInstance().addRecipeForUser(detailsFromResponse.getUserObjectId(), detailsFromResponse);
        }
    }

    private RecipeDetails getRecipeDetailsFromMessage(String recipeMessage) {
        Gson gson = new Gson();
        JsonElement gcmMessageElement = gson.fromJson(recipeMessage, JsonElement.class);
        JsonObject gcmMessageElementAsJsonObject = gcmMessageElement.getAsJsonObject();
        return ServicesUtils.createRecipeDetailsFromResponse(gson, gcmMessageElementAsJsonObject);
    }
}
