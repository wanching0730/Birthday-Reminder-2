package com.wanching.birthdayreminder.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.wanching.birthdayreminder.Fragments.AllBirthdayFragment;
import com.wanching.birthdayreminder.Fragments.UpcomingBirthdayFragment;

/**
 * Created by WanChing on 6/8/2017.
 */

/**
 * FragmentPagerAdapter for handling Swiping TabLayout
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter{

    private Context context;
    SparseArray<Fragment> sparseArray = new SparseArray<>();

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UpcomingBirthdayFragment();
            case 1:
                return new AllBirthdayFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)
            return "Upcoming";
        else
            return "All";
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment)super.instantiateItem(container, position);
        sparseArray.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        sparseArray.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getTargetFragment(int position){
        return sparseArray.get(position);
    }
}
