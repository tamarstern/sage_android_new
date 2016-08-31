package com.sage.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sage.backgroundServices.BackgroundServicesScheduler;
import com.sage.utils.ActivityUtils;

public class TabbedSearchActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private EditText searchEditText;

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_search);

        BackgroundServicesScheduler.scheduleAlarmBackgroundServices(this);
        ActivityUtils.forceInitFollowingList(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.search_recipes_lbl));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.search_users_lbl));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final PagerAdapter pageAdapter = new PageAdapter
                (getSupportFragmentManager());
        mViewPager.setAdapter(pageAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
                performSearch(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initSupportActionBar();

    }

    private void performSearch(int position) {
        PageAdapter adapter = (PageAdapter)mViewPager.getAdapter();
        Fragment fragment = adapter.getItem(position);
        if(fragment instanceof ISearchableFragment) {
            Editable editableText = searchEditText.getEditableText();
            if(editableText != null) {
                ((ISearchableFragment) fragment).onSearchClicked(editableText.toString());
            }
        }
    }

    private void initSupportActionBar() {
        final Activity activity = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayUseLogoEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(false);
        supportActionBar.setDisplayShowCustomEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(false);


        View customNav = LayoutInflater.from(this).inflate(R.layout.search_layout, null);
        supportActionBar.setCustomView(customNav);
        Toolbar parent = (Toolbar)customNav.getParent();
        parent.setContentInsetsAbsolute(0,0);

        searchEditText = (EditText) customNav.findViewById(R.id.search_text);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ActivityUtils.hideSoftKeyboard(activity);
                    int position = tabLayout.getSelectedTabPosition();
                    performSearch(position);
                    return true;
                }
                return false;
            }
        });
        ImageView searchImageView = (ImageView) customNav.findViewById(R.id.search_user_icon);
        searchImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityUtils.hideSoftKeyboard(activity);
                int position = tabLayout.getSelectedTabPosition();
                performSearch(position);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }
}
