package com.sage.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.sage.activities.ProgressDialogContainer;
import com.sage.activities.R;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.application.UserFollowingContainer;
import com.sage.backgroundServices.BackgroundServicesScheduler;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.GetFollowingService;
import com.sage.tasks.BaseFetchUsersTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tamar.twena on 8/27/2016.
 */
public class FollowingFragment extends Fragment  {


    private ListView listView;

    private RelativeLayout failedToLoadPanel;

    int pageNumber = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_following_users, container, false);

        listView = (ListView) inflate.findViewById(android.R.id.list);

        BackgroundServicesScheduler.scheduleAlarmBackgroundServices(getActivity());

        failedToLoadPanel = (RelativeLayout)inflate.findViewById(R.id.failed_to_load_panel);
        failedToLoadPanel.setVisibility(View.GONE);
        failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedToLoadPanel.setVisibility(View.GONE);
                fetchUsers();
            }
        });



        fetchUsers();
        return inflate;
    }

    private void initAdapter(List<User> users) {
        Collections.sort(users);
        UsersArrayAdapter adapter = new UsersArrayAdapter(getActivity(), users);
        listView.setAdapter(adapter);
    }


    private void fetchUsers() {

        Intent intent = getActivity().getIntent();
        String userName = (String) intent.getSerializableExtra(ActivityConstants.USER_OBJECT_ID);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String loggedInUserObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

        boolean isLoggedInUser = userName.equals(loggedInUserObjectId);

        if(isLoggedInUser && UserFollowingContainer.getInstance().followingInitialized()) {
            ArrayList<User> users = UserFollowingContainer.getInstance().getUsers();
            initAdapter(users);
            return;
        }

        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String[] params = new String[] { token, userName, Integer.toString(pageNumber) };

        new GetFollowingTask(isLoggedInUser,getActivity()).execute(params);
    }

    private class GetFollowingTask extends BaseFetchUsersTask {

        private Activity activity;
        private ProgressDialogContainer container;
        private boolean isLoggedInUser;

        public GetFollowingTask(boolean isLoggedInUser, Activity activity) {
            super(activity);
            this.activity = activity;
            container = new ProgressDialogContainer(activity);
            this.isLoggedInUser = isLoggedInUser;
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
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
            initAdapter(users);
            if(isLoggedInUser) {
                UserFollowingContainer.getInstance().putUsers(new ArrayList<User>(users));
            }

        }

        @Override
        protected void performCustomActionsOnException() {
            container.dismissProgress();
            failedToLoadPanel.setVisibility(View.VISIBLE);
        }

        @Override
        protected void performCustomActionsOnPostExecute() {
            container.dismissProgress();
        }

    }

}

