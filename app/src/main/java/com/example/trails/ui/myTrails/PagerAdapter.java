package com.example.trails.ui.myTrails;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.trails.R;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Trail;
import com.example.trails.ui.explore.ExploreFragment;
import com.example.trails.ui.explore.ExploreTrailAdapter;
import com.example.trails.ui.start.StartTrailAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import static com.example.trails.controller.DB.db;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private Context context;


    private FragmentTabMyTrails fragmentTabMyTrails;
    private Bundle bundleFragment;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        /*FragmentTabMyTrails fragmentTabMyTrails = new FragmentTabMyTrails();
        Bundle bundleFragment = new Bundle();
        bundleFragment.putInt("position",position);
        fragmentTabMyTrails.setArguments(bundleFragment);
        return fragmentTabMyTrails;*/

        fragmentTabMyTrails = new FragmentTabMyTrails();
        bundleFragment = new Bundle();

        switch (position) {
            case 0: // My trails
                bundleFragment.putInt("position",0);
                break;
            case 1: // Download
                bundleFragment.putInt("position",1);
                break;
            case 2: //Historico
                bundleFragment.putInt("position",2);
                break;
            case 3: // Favoritos
                bundleFragment.putInt("position",3);
                break;
            default:
                bundleFragment.putInt("position",4);
        }

        fragmentTabMyTrails.setArguments(bundleFragment);
        return fragmentTabMyTrails;
    }

    @Override
    public int getCount() {
        return 4;
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

