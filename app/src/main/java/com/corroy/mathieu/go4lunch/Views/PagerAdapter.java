package com.corroy.mathieu.go4lunch.Views;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.corroy.mathieu.go4lunch.Fragments.ListViewFragment;
import com.corroy.mathieu.go4lunch.Fragments.MapViewFragment;
import com.corroy.mathieu.go4lunch.Fragments.WorkmatesFragment;

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
                return WorkmatesFragment.newInstance();
            default:
                return MapViewFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
