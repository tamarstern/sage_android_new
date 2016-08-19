package com.sage.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.SearchFollowedByService;
import com.sage.tasks.BaseFetchUsersTask;
import com.sage.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchFollowedByActivity extends AppCompatActivity {

    private ListView listView;

    private EditText searchEditText;

    private ImageView searchImageView;

    private int pageNumber;

    private TextView noUsersMatchCriteria;

    RelativeLayout failedToLoadPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_followed_by);

        listView = (ListView) findViewById(android.R.id.list);

        noUsersMatchCriteria = (TextView) findViewById(R.id.no_users_found_matching_search_criteria);
        noUsersMatchCriteria.setVisibility(View.GONE);
        failedToLoadPanel = (RelativeLayout)findViewById(R.id.failed_to_load_panel);
        failedToLoadPanel.setVisibility(View.GONE);
        failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedToLoadPanel.setVisibility(View.GONE);
                noUsersMatchCriteria.setVisibility(View.GONE);
                fetchUsers();
            }
        });


        initSupportActionBar();

    }

    private void initSupportActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayUseLogoEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(false);
        supportActionBar.setDisplayShowCustomEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(false);

        View customNav = LayoutInflater.from(this).inflate(R.layout.search_followed_by_layout, null);
        supportActionBar.setCustomView(customNav);

        searchEditText = (EditText) customNav.findViewById(R.id.search_text);

        final Activity activity = this;

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    fetchUsers();
                    ActivityUtils.hideSoftKeyboard(activity);
                    return true;
                }
                return false;
            }
        });


        searchImageView = (ImageView) customNav.findViewById(R.id.search_my_follow_icon);

        searchImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fetchUsers();
                ActivityUtils.hideSoftKeyboard(activity);
            }

        });
    }

    private void fetchUsers() {
        noUsersMatchCriteria.setVisibility(View.GONE);
        failedToLoadPanel.setVisibility(View.GONE);
        if (searchEditText.getEditableText() == null
                || TextUtils.isEmpty(searchEditText.getEditableText().toString())) {
            Toast.makeText(this, R.string.did_not_enter_search_text, Toast.LENGTH_SHORT).show();
            return;
        }

        initListAdapter(new ArrayList<User>());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

        Editable editableText = searchEditText.getEditableText();

        String searchText = editableText.toString();

        String[] params = new String[] { token, userName , Integer.toString(pageNumber)};

        new SearchFollowedByTask(this, searchText).execute(params);
    }

    private void initListAdapter(List<User> users) {
        UsersArrayAdapter adapter = new UsersArrayAdapter(this, users);
        listView.setAdapter(adapter);
    }


    private class SearchFollowedByTask extends BaseFetchUsersTask {

        private Activity activity;
        private ProgressDialogContainer container;
        private String searchText;

        public SearchFollowedByTask(Activity activity, String searchText) {
            super(activity);
            this.activity = activity;
            container = new ProgressDialogContainer(activity);
            this.searchText = searchText;
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
        }

        @Override
        protected JsonElement createAndExecuteService(String currentToken, String userName, int pageNumber) throws Exception {
            SearchFollowedByService service = new SearchFollowedByService(currentToken, userName, searchText, pageNumber);
            return service.getUsers();

        }

        @Override
        protected void handleNoUsersFound() {
            noUsersMatchCriteria.setVisibility(View.VISIBLE);
        }

        @Override
        protected void initializeUi(List<User> users) {
            initListAdapter(users);

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
