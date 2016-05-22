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
import com.sage.application.UserFollowingContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.FollowUserService;
import com.sage.services.UnfollowUserService;

import java.util.HashSet;

/**
 * Created by tamar.twena on 4/20/2016.
 */
public class SyncFollowAndUnfollowUsers extends IntentService {


    public SyncFollowAndUnfollowUsers() {
        super("SyncFollowAndUnfollow");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("syncFollow", "start sync follow backgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);
            if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                followUsers(token, userName);
                unfollowUsers(token, userName);
            }


        } catch (Exception e) {
            Log.e("failed sync following", "failed sync following", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    private void followUsers(String token, String userName) throws Exception {
        HashSet<User> usersToFollow = UserFollowingContainer.getInstance().getUsersToFollow();
        if(usersToFollow != null && usersToFollow.size() > 0) {
            HashSet<User> followingCompleted = new HashSet<User>();
            for(User user : usersToFollow) {
                String usernameToFollow = user.getUsername();
                FollowUserService service = new FollowUserService(token, userName, usernameToFollow, null);
                JsonElement jsonElement = service.followUser();
                JsonObject resultJsonObject = jsonElement.getAsJsonObject();
                boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
                    if (requestSuccess) {
                        Log.i("syncFollow", "start follow user " + user.getUsername());
                        followingCompleted.add(user);

                    }
                }
            usersToFollow.removeAll(followingCompleted);
            UserFollowingContainer.getInstance().putUsersToFollow(usersToFollow);
        }
    }

    private void unfollowUsers(String token, String userName) throws Exception {
        HashSet<User> usersToUnfollow = UserFollowingContainer.getInstance().getUsersToUnfollow();
        if(usersToUnfollow != null && usersToUnfollow.size() > 0) {
            HashSet<User> followingCompleted = new HashSet<User>();
            for(User user : usersToUnfollow) {
                String userToUnfollow = user.getUsername();
                UnfollowUserService service = new UnfollowUserService(token, userName, userToUnfollow, null);
                JsonElement jsonElement = service.unfollowUser();
                JsonObject resultJsonObject = jsonElement.getAsJsonObject();
                boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
                if (requestSuccess) {
                    Log.i("syncFollow", "start unfollow user " + user.getUsername());
                    followingCompleted.add(user);

                }
            }
            usersToUnfollow.removeAll(followingCompleted);
            UserFollowingContainer.getInstance().putUsersToUnfollow(usersToUnfollow);
        }
    }
}
