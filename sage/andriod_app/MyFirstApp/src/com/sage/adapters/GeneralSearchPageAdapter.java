package com.sage.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.sage.fragments.SearchRecipeFragment;
import com.sage.fragments.SearchUserFragment;

import java.util.HashMap;

/**
 * Created by tamar.twena on 8/27/2016.
 */
public class GeneralSearchPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs = 2;

    private HashMap<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

    public GeneralSearchPageAdapter(FragmentManager fm) {
        super(fm);
        SearchRecipeFragment searchRecipeFragment = new SearchRecipeFragment();
        fragmentMap.put(0, searchRecipeFragment);
        SearchUserFragment searchUserFragment = new SearchUserFragment();
        fragmentMap.put(1, searchUserFragment);

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


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        fragmentMap.remove(position);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }



}