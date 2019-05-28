package com.example.studygroups;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private Fragment[] childFragments;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        childFragments = new Fragment[] {
                new ActiveGroup(), //0
                new ClosedGroup(), //1
        };
    }

    @Override
    public Fragment getItem(int position) {
        return childFragments[position];
    }

    @Override
    public int getCount() {
        return 2; //three fragments
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "Active Groups";
        }
        return "Closed Groups";

    }

}