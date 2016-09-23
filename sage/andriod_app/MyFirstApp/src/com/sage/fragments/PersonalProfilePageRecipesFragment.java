package com.sage.fragments;

import android.app.Activity;
import android.content.Intent;
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

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sage.activities.R;
import com.sage.adapters.NewsfeedArrayAdapter;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.NewsfeedContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.GetPublishedRecipesForUser;
import com.sage.tasks.GetRecipiesActivity;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tamar.twena on 8/27/2016.
 */
public class PersonalProfilePageRecipesFragment extends Fragment {


    private ListView listView;

    private int pageNumber = 0;
    private int preLast = 0;
    ;
    private volatile boolean loadingMore = false;
    private boolean afterStop;
    private boolean shouldIncreasePage = true;

    private RelativeLayout failedToLoadPanel;

    private NewsfeedArrayAdapter adapter;

    private TextView noPublishedRecipesMyProfile;
    private TextView noPublishedRecipesOtherUserProfile;
    private ProgressBar progressBar;
    private boolean currentUserProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_profile_recipes, container, false);

        Intent i = getActivity().getIntent();
        currentUserProfile = (boolean) i.getBooleanExtra(EntityDataTransferConstants.OPEN_USER_PROFILE, false);


        noPublishedRecipesMyProfile = (TextView) inflate.findViewById(R.id.no_published_recipes_my_profile);
        noPublishedRecipesMyProfile.setVisibility(View.GONE);

        noPublishedRecipesOtherUserProfile = (TextView) inflate.findViewById(R.id.no_published_recipes_other_user_profile);
        noPublishedRecipesOtherUserProfile.setVisibility(View.GONE);

        failedToLoadPanel = (RelativeLayout) inflate.findViewById(R.id.failed_to_load_panel);
        failedToLoadPanel.setVisibility(View.GONE);
        failedToLoadPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedToLoadPanel.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                getAllRecipiesForUser();
            }
        });


        View footer = inflater.inflate(R.layout.progress_bar_footer, null);
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

            adapter = new NewsfeedArrayAdapter(getActivity(), new ArrayList<RecipeDetails>());
            listView.setAdapter(adapter);
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (afterStop) {
                    afterStop = false;
                    return;
                }

                if (loadingMore) {
                    return;
                }
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount -1)) {
                    if (preLast == 0 || preLast != lastInScreen) {
                        preLast = lastInScreen;
                        getAllRecipiesForUser();
                    }

                }
            }
        });
    }

    private void getAllRecipiesForUser() {
        if(currentUserProfile) {
            ArrayList<RecipeDetails> recipesByPage = MyProfileRecipiesContainer.getInstance().getRecipesByPage(pageNumber);
            if( recipesByPage!= null && MyProfileRecipiesContainer.getInstance().isMyProfileRecipesInitialized()) {
                if(recipesByPage.size() > 0) {
                    initAdaptor(recipesByPage);
                } else if(pageNumber == 0) {
                    noPublishedRecipesMyProfile.setVisibility(View.VISIBLE);
                }
                return;
            }
        }
        if(!currentUserProfile && pageNumber == 0) {
            Intent i = getActivity().getIntent();
            String recipeAuthor = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
            ArrayList<RecipeDetails> recipesForUser = NewsfeedContainer.getInstance().getProfileForUser(recipeAuthor);
            if(recipesForUser != null && recipesForUser.size() > 0 ) {
                initAdaptor(recipesForUser);
                pageNumber +=1;
                return;
            }
        }
        initRecipiesForUser();
    }

    private void initRecipiesForUser() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
        String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
        Intent i = getActivity().getIntent();
        String recipeAuthor = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
        recipeAuthor = TextUtils.isEmpty(recipeAuthor) ? userName : recipeAuthor;
        new GetRecipiesTask<RecipeDetails>(getActivity(), recipeAuthor).execute(token, userName, pageNumber);
    }

    private void initAdaptor(ArrayList<RecipeDetails> details) {
        Iterator iterator = details.iterator();
        while (iterator.hasNext()) {
            adapter.add((RecipeDetails) iterator.next());
        }
        if(shouldIncreasePage) {
            pageNumber += 1;
        }
        listView.setAdapter(adapter);
    }



    private class GetRecipiesTask<T extends RecipeDetails> extends GetRecipiesActivity {

        private String recipeAuthor;

        GetRecipiesTask(Activity context, String recipeAuthor) {
            super(new TypeToken<ArrayList<RecipeDetails>>() {
            }.getType(), context);
            this.recipeAuthor = recipeAuthor;
        }

        @Override
        protected JsonElement doInBackground(Object... token) {
            return super.doInBackground(token);
        }

        @Override
        protected void performCustomActionsOnPreExecute() {
            loadingMore = true;
            shouldIncreasePage = true;
            if (listView.getAdapter().getCount()-1 > 0) {
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
            } else if (listView.getAdapter().getCount()-1 > 0) {
                progressBar.setVisibility(View.GONE);
            } else {
                super.performCustomActionsOnException();
            }
        }

        @Override
        protected void performCustomActionsOnPostExecute() {
            loadingMore = false;
            if (listView.getAdapter().getCount()-1 > 0) {
                progressBar.setVisibility(View.GONE);
            } else {
                super.performCustomActionsOnPostExecute();
            }
        }

        @Override
        protected void onPostExecute(JsonElement result) {
            super.onPostExecute(result);
            if (details != null) {
                if((pageNumber == 0 || pageNumber ==1) && currentUserProfile) {
                    MyProfileRecipiesContainer.getInstance().putRecipesForPage(pageNumber, details);
                    MyProfileRecipiesContainer.getInstance().setMyProfileRecipesInitialized(true);
                } else if(!currentUserProfile && pageNumber == 0) {
                    NewsfeedContainer.getInstance().addProfileRecipesForUser(recipeAuthor, details);
                }

                initAdaptor(details);
                listView.setVisibility(View.VISIBLE);
                loadingMore = false;

            }
        }

        @Override
        protected void handleNoRecipesFound() {
            if(pageNumber == 0) {
                if(currentUserProfile) {
                    noPublishedRecipesMyProfile.setVisibility(View.VISIBLE);
                } else {
                    noPublishedRecipesOtherUserProfile.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        protected JsonElement CreateAndExecuteService(String currentToken, String userName, int pageNumber)
                throws Exception {
            GetPublishedRecipesForUser service = new GetPublishedRecipesForUser(currentToken, userName, recipeAuthor,
                    pageNumber);
            return service.getRecipies();
        }

    }






}

