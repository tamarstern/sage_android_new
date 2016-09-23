package com.sage.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sage.fragments.FollowingFragment;
import com.sage.fragments.PersonalProfilePageRecipesFragment;

import java.util.HashMap;

/**
 * Created by tamar.twena on 8/27/2016.
 */
public class PersonalProfilePageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 2;

    private HashMap<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

    public PersonalProfilePageAdapter(FragmentManager fm) {
        super(fm);
        PersonalProfilePageRecipesFragment recipeFragment = new PersonalProfilePageRecipesFragment();
        fragmentMap.put(0, recipeFragment);
        FollowingFragment followingFragment = new FollowingFragment();
        fragmentMap.put(1, followingFragment);


    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return fragmentMap.get(0);
            case 1:
                return fragmentMap.get(1);
            default:
                return null;
        }
    }

    public void onStop() {
        fragmentMap.get(0).onStop();
        fragmentMap.get(1).onStop();
    }



    @Override
    public int getCount() {
        return mNumOfTabs;
    }





}