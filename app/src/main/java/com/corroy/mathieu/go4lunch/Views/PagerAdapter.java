package com.corroy.mathieu.go4lunch.Views;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.corroy.mathieu.go4lunch.Fragments.ListViewFragment;
import com.corroy.mathieu.go4lunch.Fragments.MapViewFragment;
import com.corroy.mathieu.go4lunch.Fragments.Workmates;

public class PagerAdapter extends FragmentPagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {

        switch(pos){

            case 0:
                return MapViewFragment.newInstance();
            case 1:
                return ListViewFragment.newInstance();
            case 2:
                return Workmates.newInstance();
            default:
                return MapViewFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
