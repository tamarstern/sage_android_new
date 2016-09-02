package com.sage.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.sage.application.UserFollowingContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.GetFollowingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamar.twena on 8/31/2016.
 */
public class FollowingListBackgroundInitializer {


    public void fetchUsers(Activity activity) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String loggedInUserObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        if(TextUtils.isEmpty(loggedInUserObjectId) || TextUtils.isEmpty(token)) {
            return;
        }

        String[] params = new String[] { token, loggedInUserObjectId, Integer.toString(0) };

        new GetFollowingTask(activity).execute(params);
    }



    private class GetFollowingTask extends BaseFetchUsersTask {

        private Activity activity;

        public GetFollowingTask(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JsonElement createAndExecuteService(String currentToken, String userName, int pageNumber)
                throws Exception {
            GetFollowingService service = new GetFollowingService(currentToken, userName, pageNumber);
            return service.getUsers();

        }

        @Override
        protected void handleNoUsersFound() {

        }

        @Override
        protected void initializeWhenFoundUsers(List<User> users) {
            UserFollowingContainer.getInstance().putUsers(new ArrayList<User>(users));

        }

        @Override
        protected void performCustomActionsOnException() {

        }

        @Override
        protected void performCustomActionsOnPostExecute() {

        }

    }

}
