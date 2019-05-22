package com.eli.orange.adapter;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.eli.orange.fragments.LocationDataFragment;
import com.eli.orange.fragments.SettingsFragment;
import com.eli.orange.fragments.addCenter.AddCenterFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Bundle bundle=new Bundle();
                bundle.putString("message", "From Activity");
                LocationDataFragment locationDataFragment = new LocationDataFragment();
                locationDataFragment.setArguments(bundle);
                return locationDataFragment;
            case 1:
                return new SettingsFragment();
            case 2:
                return new SettingsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}