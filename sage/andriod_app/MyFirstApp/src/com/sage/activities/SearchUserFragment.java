package com.sage.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.sage.adapters.UsersArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.User;
import com.sage.services.SearchUsersService;
import com.sage.tasks.BaseFetchUsersTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamar.twena on 8/27/2016.
 */
public class SearchUserFragment extends Fragment implements ISearchableFragment {


    private ListView listView;

    private int pageNumber;

    private TextView noUsersFoundThatMatchCriteria;

    private RelativeLayout failedToLoadPanel;

    private String currentTextToSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_tabbed_search_users, container, false);

        listView = (ListView) inflate.findViewById(android.R.id.list);

        noUsersFoundThatMatchCriteria = (TextView)inflate.findViewById(R.id.no_users_found_matching_search_criteria);
        noUsersFoundThatMatchCriteria.setVisibility(View.GONE);
        failedToLoadPanel = (RelativeLayout)inflate.findViewById(R.id.failed_to_load_panel);
        failedToLoadPanel.setVisibility(View.GONE);
        failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedToLoadPanel.setVisibility(View.GONE);
                noUsersFoundThatMatchCriteria.setVisibility(View.GONE);
                fetchUsers(currentTextToSearch);
            }
        });
        return inflate;
    }

    private void fetchUsers(String textToSearch) {
        if (TextUtils.isEmpty(textToSearch)) {
            Toast.makeText(getActivity(), R.string.did_not_enter_search_text, Toast.LENGTH_SHORT).show();
            return;
        }

        initListAdapter(new ArrayList<User>());

        noUsersFoundThatMatchCriteria.setVisibility(View.GONE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String[] params = new String[] { token, textToSearch, Integer.toString(pageNumber) };

        new SearchUsersTask(getActivity()).execute(params);

    }

    private void initListAdapter(List<User> users) {
        UsersArrayAdapter adapter = new UsersArrayAdapter(getActivity(), users);
        listView.setAdapter(adapter);
    }

    private class SearchUsersTask extends BaseFetchUsersTask {

        private Activity activity;
        private ProgressDialogContainer container;

        public SearchUsersTask(Activity activity) {
            super(activity);
            this.activity = activity;
            container = new ProgressDialogContainer(activity);
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
        }

        @Override
        protected JsonElement createAndExecuteService(String currentToken, String textToSearch, int pageNumber)
                throws Exception {
            SearchUsersService service = new SearchUsersService(currentToken, textToSearch, pageNumber);
            return service.getUsers();

        }

        @Override
        protected void handleNoUsersFound() {
            noUsersFoundThatMatchCriteria.setVisibility(View.VISIBLE);
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


    @Override
    public void onSearchClicked(String textToSearch) {
        this.currentTextToSearch = textToSearch;
        fetchUsers(textToSearch);
    }
}

