package com.wanching.birthdayreminder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    public SimpleFragmentPagerAdapter (Context context, FragmentManager fm){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new UpcomingBirthdayFragment();
            case 1: return new AllBirthdayActivityFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {return 2; }

    @Override
    public CharSequence getPageTitle(int position) {
       if(position == 0)
           return "Upcoming";
        else
            return "All";
    }
}
