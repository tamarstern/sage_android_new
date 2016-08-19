package com.sage.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.GetFollowedByService;
import com.sage.tasks.BaseFetchUsersTask;

import java.util.Collections;
import java.util.List;

public class DisplayFollowedByActivity extends AppCompatActivity {

    private RelativeLayout failedToLoadPanel;
    private ListView listView;
    private int pageNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_followed_by);

        listView = (ListView) findViewById(android.R.id.list);

        failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
        failedToLoadPanel.setVisibility(View.GONE);
        failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedToLoadPanel.setVisibility(View.GONE);
                fetchUsers();
            }
        });


        initSupportActionBar();
        fetchUsers();
    }

    private void initSupportActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.followed_by_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_followed_by_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search_user) {
            Intent intent = new Intent(this, SearchFollowedByActivity.class);
            startActivity(intent);
            return true;
        } else {

            return super.onOptionsItemSelected(item);

        }
    }



    private void fetchUsers() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String loggedInUserObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String[] params = new String[] { token, loggedInUserObjectId, Integer.toString(pageNumber) };

        new GetFollowedByTask(this).execute(params);
    }

    private void initAdapter(List<User> users) {
        Collections.sort(users);
        UsersArrayAdapter adapter = new UsersArrayAdapter(this, users);
        listView.setAdapter(adapter);
    }



    private class GetFollowedByTask extends BaseFetchUsersTask {

        private Activity activity;
        private ProgressDialogContainer container;

        public GetFollowedByTask( Activity activity) {
            super(activity);
            this.activity = activity;
            container = new ProgressDialogContainer(activity);
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
        }

        @Override
        protected JsonElement createAndExecuteService(String currentToken, String userName, int pageNumber)
                throws Exception {
            GetFollowedByService service = new GetFollowedByService(currentToken, userName, pageNumber);
            return service.getUsers();

        }

        @Override
        protected void handleNoUsersFound() {

        }

        @Override
        protected void initializeUi(List<User> users) {
            initAdapter(users);
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
