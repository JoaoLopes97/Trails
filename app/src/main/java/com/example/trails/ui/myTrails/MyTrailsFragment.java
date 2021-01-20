package com.example.trails.ui.myTrails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.trails.R;
import com.google.android.material.tabs.TabLayout;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MyTrailsFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.my_trails_fragment, container, false);
        TabLayout tabLayout = root.findViewById(R.id.tabLayout);

        ViewPager viewPager = root.findViewById(R.id.viewPager);
        List<Fragment> fragments = new ArrayList<>();

        fragments.add(new FragmentTabMyTrails(0));
        fragments.add(new FragmentTabDownloaded());
        fragments.add(new FragmentTabMyTrails(2));
        fragments.add(new FragmentTabMyTrails(3));
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), this.getContext(),fragments));
        tabLayout.setupWithViewPager(viewPager);


        return root;
    }

}