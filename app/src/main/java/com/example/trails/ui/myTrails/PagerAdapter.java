package com.example.trails.ui.myTrails;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.trails.R;

import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private Context context;
    List<Fragment> fragments;

    public PagerAdapter(FragmentManager fm, Context context, List<Fragment> fragmentList) {
        super(fm);
        this.context = context;
        this.fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
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

