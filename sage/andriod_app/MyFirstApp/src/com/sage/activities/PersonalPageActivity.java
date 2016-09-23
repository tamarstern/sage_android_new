package com.sage.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sage.adapters.PersonalProfilePageAdapter;
import com.sage.application.UserFollowingContainer;
import com.sage.backgroundServices.BackgroundServicesScheduler;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.User;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;

import java.io.Serializable;
import java.text.MessageFormat;

public class PersonalPageActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private TabLayout tabLayout;

    private boolean currentUserProfile;

    private MenuItem followUserMenuItem;

    private MenuItem unfollowUserMenuItem;

    private String userDisplayName;

    private PersonalProfilePageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);

        BackgroundServicesScheduler.scheduleAlarmBackgroundServices(this);
        ActivityUtils.forceInitFollowingList(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.profile_recipes_lbl));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.profile_following_lbl));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        pageAdapter = new PersonalProfilePageAdapter
                (getSupportFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Intent i = getIntent();
        currentUserProfile = (boolean) i.getBooleanExtra(EntityDataTransferConstants.OPEN_USER_PROFILE, false);

        initSupportActionBar();

        if (currentUserProfile) {
            AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.OPEN_MY_PROFILE);
        } else {
            AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.VIEW_OTHER_USER_PROFILE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setupMenu(menu);

        return super.onCreateOptionsMenu(menu);

    }

    private void setupMenu(Menu menu) {
        if (!currentUserProfile) {
            getMenuInflater().inflate(R.menu.user_personal_page_menu, menu);
            followUserMenuItem = menu.findItem(R.id.action_follow);
            followUserMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);;
            unfollowUserMenuItem = menu.findItem(R.id.action_unfollow);
            unfollowUserMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);;
        } else {
            getMenuInflater().inflate(R.menu.menu_my_personal_page, menu);
        }

        InitProfileDisplay();
    }

    @Override
    protected void onStop() {
        pageAdapter.onStop();
        super.onStop();
    }

    private void InitProfileDisplay() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Intent i = getIntent();
        Serializable transferedUserDisplayName = i
                .getSerializableExtra(EntityDataTransferConstants.USER_DISPLAY_NAME_DATA_TRANSFER);
        iniActionBarWithUserName(sharedPref, transferedUserDisplayName);
        if (!currentUserProfile) {
            boolean isFollowing = (boolean)i.getSerializableExtra(EntityDataTransferConstants.IS_FOLLOWING);
            initFollowButtonsVisibility(isFollowing);
        }

        else {

            Serializable countTransfered = getIntent().getSerializableExtra(EntityDataTransferConstants.FOLLOW_BY_COUNT);
            String count = (countTransfered == null ? "" :countTransfered.toString()) ;

            initSupportActionBarTitleWithFollowersNumber(count);
        }

    }

    private void initSupportActionBarTitleWithFollowersNumber(String followedByCount) {
        if (TextUtils.isEmpty(followedByCount)) {
            return;
        }

        String currentText = getResources().getString(R.string.user_followed_by);
        String followedByText = MessageFormat.format("{0}: {1}", currentText, followedByCount);
        ActionBar supportActionBar = getSupportActionBar();
        String currentTitle = supportActionBar.getTitle().toString();
        currentTitle = MessageFormat.format("{0}   ({1})", currentTitle, followedByText);
        supportActionBar.setTitle(currentTitle);
    }


    private void iniActionBarWithUserName(SharedPreferences sharedPref, Serializable transferedUserDisplayName) {
        ActionBar supportActionBar = getSupportActionBar();
        if (transferedUserDisplayName != null) {
            this.userDisplayName = transferedUserDisplayName.toString();
            supportActionBar.setTitle(userDisplayName);
        } else {
            userDisplayName = sharedPref.getString(ActivityConstants.USER_DISPLAY_NAME, null);
            supportActionBar.setTitle(userDisplayName);
        }
    }



    private void initFollowButtonsVisibility(boolean isFollowing) {
         if (isFollowing) {
            followUserMenuItem.setVisible(false);
            unfollowUserMenuItem.setVisible(true);
        } else {
            followUserMenuItem.setVisible(true);
            unfollowUserMenuItem.setVisible(false);
        }
    }

    private void followUser() {
        User user = createUserObject();
        UserFollowingContainer.getInstance().follow(user);
        initFollowButtonsVisibility(true);
        AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PRESS_FOLLOW_USER);

    }

    @NonNull
    private User createUserObject() {
        Intent i = getIntent();
        User user = new User();
        user.setUserDisplayName(this.userDisplayName);
        String userId = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_OBJECT_ID_DATA_TRANSFER);
        user.set_id(userId);
        String username = (String) i.getSerializableExtra(EntityDataTransferConstants.USER_NAME_DATA_TRANSFER);
        user.setUsername(username);
        return user;
    }

    private void unfollowUser() {
        User user = createUserObject();
        UserFollowingContainer.getInstance().unFollow(user);
        initFollowButtonsVisibility(false);
        AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PRESS_UNFOLLOW_USER);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_follow) {
            followUser();
            return true;
        }
        else if (item.getItemId() == R.id.action_unfollow) {
            unfollowUser();
            return true;
        }
        else if (item.getItemId() == R.id.action_search) {
            Intent searchIntent = new Intent(this, TabbedSearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initSupportActionBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.title_profile_page);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");
        final Activity activity = this;
        if(currentUserProfile) {
            myToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, DisplayFollowedByActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

}
