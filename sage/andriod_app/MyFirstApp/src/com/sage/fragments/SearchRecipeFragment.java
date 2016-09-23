package com.sage.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sage.activities.R;
import com.sage.adapters.NewsfeedArrayAdapter;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.GetSearchRecipes;
import com.sage.tasks.GetRecipiesActivity;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tamar.twena on 8/27/2016.
 */
public class SearchRecipeFragment extends Fragment implements ISearchableFragment {

    ListView listView;

    private NewsfeedArrayAdapter adapter;
    private ProgressBar progressBar;
    private boolean afterStop;

    private RelativeLayout failedToLoadPanel;


    private int pageNumber = 0;
    private int preLast = 0;
    private boolean shouldIncreasePage = true;
    private volatile boolean loadingMore = false;

    private TextView noRecipesMatchSearchCriteria;

    private String currentTextToSearch;
    private View inflate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         inflate = inflater.inflate(R.layout.fragment_tabbed_search_recipes, container, false);

        noRecipesMatchSearchCriteria = (TextView)inflate.findViewById(R.id.no_recipes_found_matching_search_criteria);
        failedToLoadPanel = (RelativeLayout)inflate.findViewById(R.id.failed_to_load_panel);
        noRecipesMatchSearchCriteria.setVisibility(View.GONE);
        failedToLoadPanel.setVisibility(View.GONE);
        failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedToLoadPanel.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                getSearchRecipies(currentTextToSearch);
            }
        });


        View footer = getActivity().getLayoutInflater().inflate(R.layout.progress_bar_footer, null);
        progressBar = (ProgressBar) footer.findViewById(R.id.get_recipies_progress);

        progressBar.setVisibility(View.GONE);
        listView = (ListView) inflate.findViewById(android.R.id.list);
        listView.addFooterView(footer);
        initListView();

        return inflate;
    }

    @Override
    public void onStop() {
        this.afterStop = true;
        super.onStop();
    }


    private void initListView() {
        if (listView.getAdapter() == null) {
            initListAdaptor();
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (TextUtils.isEmpty(currentTextToSearch)) {
                    return;
                }
                if (afterStop) {
                    afterStop = false;
                    return;
                }

                if (loadingMore) {
                    return;
                }
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount)) {
                    if (preLast != lastInScreen && !shouldIncreasePage) {
                        preLast = lastInScreen;
                        getSearchRecipies(currentTextToSearch);
                    }

                }
            }
        });
    }

    private void initListAdaptor() {
        adapter = new NewsfeedArrayAdapter(getActivity(), new ArrayList<RecipeDetails>());
        listView.setAdapter(adapter);
    }

    private void getSearchRecipies(String textToSearch) {
        if (TextUtils.isEmpty(textToSearch)) {
            Toast.makeText(getActivity(), R.string.did_not_enter_search_text, Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
        String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

        new GetRecipiesForSearch<RecipeDetails>(getActivity(), textToSearch).execute(token, userName, pageNumber);

    }

    @Override
    public void onSearchClicked(String textToSearch) {
        this.currentTextToSearch = textToSearch;
        pageNumber = 0;
        if(noRecipesMatchSearchCriteria == null && inflate != null) {
            noRecipesMatchSearchCriteria = (TextView)inflate.findViewById(R.id.no_recipes_found_matching_search_criteria);
        }
        noRecipesMatchSearchCriteria.setVisibility(View.GONE);
        initListAdaptor();
        getSearchRecipies(textToSearch);

    }

    private class GetRecipiesForSearch<T extends RecipeDetails> extends GetRecipiesActivity {

        private Activity activity;
        private String textToSearch;

        public GetRecipiesForSearch(Activity activity, String textToSearch) {
            super(new TypeToken<ArrayList<RecipeDetails>>() {
            }.getType(), activity);

            this.textToSearch = textToSearch;

        }

        @Override
        protected void performCustomActionsOnPreExecute() {
            loadingMore = true;
            shouldIncreasePage = true;
            if(pageNumber == 0) {
                super.performCustomActionsOnPreExecute();
            }else if (listView.getAdapter().getCount()-1 > 0) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                super.performCustomActionsOnPreExecute();
            }

        }

        @Override
        protected void performCustomActionsOnException() {
            loadingMore = false;
            shouldIncreasePage = false;
            if(pageNumber == 0) {
                super.performCustomActionsOnException();
                failedToLoadPanel.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }else if (listView.getAdapter().getCount()-1 > 0) {
                progressBar.setVisibility(View.GONE);
            } else {
                super.performCustomActionsOnException();
            }
        }

        @Override
        protected void performCustomActionsOnPostExecute() {
            loadingMore = false;
            if(pageNumber == 0) {
                super.performCustomActionsOnPostExecute();
            }else if (listView.getAdapter().getCount()-1 > 0) {
                progressBar.setVisibility(View.GONE);
            } else  {
                super.performCustomActionsOnPostExecute();
            }
        }

        @Override
        protected JsonElement doInBackground(Object... token) {
            return super.doInBackground(token);
        }

        @Override
        protected void onPostExecute(JsonElement result) {
            super.onPostExecute(result);
            if (details != null) {
                Iterator iterator = details.iterator();
                while (iterator.hasNext()) {
                    adapter.add((RecipeDetails) iterator.next());
                }
                adapter.notifyDataSetChanged();
                if(shouldIncreasePage) {
                    pageNumber += 1;
                    shouldIncreasePage = false;
                }
                loadingMore = false;

            }

        }

        @Override
        protected void handleNoRecipesFound() {
            if(pageNumber == 0) {
                noRecipesMatchSearchCriteria.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected JsonElement CreateAndExecuteService(String currentToken, String userName, int pageNumber)
                throws Exception {
            GetSearchRecipes service = new GetSearchRecipes(currentToken, userName, this.textToSearch, pageNumber);
            return service.getRecipies();
        }

    }


}
