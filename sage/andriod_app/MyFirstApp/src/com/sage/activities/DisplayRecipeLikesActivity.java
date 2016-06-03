package com.sage.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.JsonElement;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.User;
import com.sage.services.GetLikesService;
import com.sage.tasks.BaseFetchUsersTask;
import com.sage.activities.R;

import java.util.Collections;
import java.util.List;

public class DisplayRecipeLikesActivity extends AppCompatActivity {

    private ListView listView;
    private int pageNumber;
    private RelativeLayout failedToLoadPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe_likes);

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

    private void initSupportActionBar(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.users_who_likes_recipe);
    }

    private void initAdapter(List<User> users) {
        Collections.sort(users);
        UsersArrayAdapter adapter = new UsersArrayAdapter(this, users);
        listView.setAdapter(adapter);
    }


    private void fetchUsers() {
        Intent intent = getIntent();
        RecipeDetails recipeDerails = (RecipeDetails) intent.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String[] params = new String[] { token, recipeDerails.get_id(), Integer.toString(pageNumber) };

        new GetLikesServiceTask(this).execute(params);
    }

    private class GetLikesServiceTask extends BaseFetchUsersTask {

        private Activity activity;
        private ProgressDialogContainer container;

        public GetLikesServiceTask( Activity activity) {
            super(activity);
            this.activity = activity;
            container = new ProgressDialogContainer(activity);
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
        }

        @Override
        protected JsonElement createAndExecuteService(String currentToken, String recipeId, int pageNumber)
                throws Exception {
            GetLikesService service = new GetLikesService(currentToken, recipeId, pageNumber);
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
