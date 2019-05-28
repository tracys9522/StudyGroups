package com.example.studygroups;

import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterSearch extends FragmentStatePagerAdapter {

    int noOfTabs;

    public PagerAdapterSearch(FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.noOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                basic_search tab1 = new basic_search();
                return tab1;
            case 1:
                adv_search tab2 = new adv_search();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }

}