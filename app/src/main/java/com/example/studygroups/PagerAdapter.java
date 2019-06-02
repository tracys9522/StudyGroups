package com.example.studygroups;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

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


//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
//        // save the appropriate reference depending on position
//        switch (position) {
//            case 0:
//                m1stFragment = (ActiveGroup) createdFragment;
//                break;
//            case 1:
//                m2ndFragment = (FragmentB) createdFragment;
//                break;
//        }
//        return createdFragment;
//    }



    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "Active Groups";
        }
        return "Closed Groups";

    }

}