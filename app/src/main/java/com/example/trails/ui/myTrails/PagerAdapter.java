package com.example.trails.ui.myTrails;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.trails.R;

public class PagerAdapter extends FragmentStatePagerAdapter  {
   private Context context;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return new FragmentTabMyTrails();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch(position){
            case 0:
                return context.getResources().getString(R.string.title_my_trails);
            case 1:
                return context.getResources().getString(R.string.title_download);
            case 2:
                return context.getResources().getString(R.string.title_history);
            case 3:
                return context.getResources().getString(R.string.title_favorites);
            default:
                return null;
        }
    }
}

