package com.example.trails.ui.myTrails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.trails.R;
import com.google.android.material.tabs.TabLayout;

public class MyTrailsFragment extends Fragment {

    private MyTrailsViewModel myTrailsViewModel;
    private ViewPager viewPager;
    private TabLayout tabLayout ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_trails_fragment, container, false);
        tabLayout = root.findViewById(R.id.tabLayout);

        viewPager = root.findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), this.getContext()));
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }

}