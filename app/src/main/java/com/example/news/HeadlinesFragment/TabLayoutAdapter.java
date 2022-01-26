package com.example.news.HeadlinesFragment;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabLayoutAdapter extends FragmentPagerAdapter {
    Context mContext;
    int mTotalTabs;

    public TabLayoutAdapter(Context context, FragmentManager fragmentManager, int totalTabs) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("asasas", position + "");
        switch (position) {
            case 0:
                return new GeneralFragment();
            case 1:
                return new CountryFragment();
            case 2:
                return new EntertainmentFragment();
            case 3:
                return new BusinessFragment();
            case 4:
                return new HealthFragment();
            case 5:
                return new ScienceFragment();
            case 6:
                return new SportsFragment();
            case 7:
                return new TechnologyFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}
