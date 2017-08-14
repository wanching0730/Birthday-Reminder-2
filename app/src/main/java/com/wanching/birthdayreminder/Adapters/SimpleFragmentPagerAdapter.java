package com.wanching.birthdayreminder.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

<<<<<<< HEAD:app/src/main/java/com/wanching/birthdayreminder/SimpleFragmentPagerAdapter.java
import com.wanching.birthdayreminder.Fragments.UpcomingBirthdayFragment;

||||||| merged common ancestors
=======
import com.wanching.birthdayreminder.Fragments.AllBirthdayActivityFragment;
import com.wanching.birthdayreminder.Fragments.UpcomingBirthdayFragment;

>>>>>>> networking:app/src/main/java/com/wanching/birthdayreminder/Adapters/SimpleFragmentPagerAdapter.java
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
