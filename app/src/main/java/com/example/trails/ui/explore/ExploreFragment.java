package com.example.trails.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trails.R;
import com.example.trails.model.Coordinates;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import static com.example.trails.MainActivity.setFragment;

public class ExploreFragment extends Fragment {

    private FloatingActionButton viewMode;
    private ListFragment listFragment;
    private MapFragment mapFragment;
    private FilterFragment filterFragment;
    private View filterLayout;
    public static HashMap<String, Coordinates> trails = new HashMap<>();

    private boolean viewMode_Map = false; // true if viewMode is Map, false if viewMode is List.
    private boolean showingFilter = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.explore_fragment, container, false);

        setFragment(R.id.explore_frag, new ListFragment(), getActivity());
        viewMode = root.findViewById(R.id.view_mode);
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewMode();
            }
        });

        final FloatingActionButton filterBtn = root.findViewById(R.id.filter);

        filterLayout = root.findViewById(R.id.filter_frag);

        filterFragment = new FilterFragment();

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showingFilter) {
                    setFragment(R.id.filter_frag, filterFragment, getActivity());
                    filterLayout.setClickable(true);
                    showingFilter = true;
                } else {
                    hideFilter();
                    filterLayout.setClickable(false);
                    showingFilter = false;
                }
            }
        });

        return root;
    }

    private void changeViewMode() {

        if (viewMode_Map) {
            viewMode.setImageResource(R.drawable.ic_baseline_map_24);
            viewMode_Map = false;

            listFragment = new ListFragment();
            setFragment(R.id.explore_frag, listFragment, getActivity());
        } else {
            viewMode.setImageResource(R.drawable.ic_baseline_list_24);
            viewMode_Map = true;

            mapFragment = new MapFragment();
            setFragment(R.id.explore_frag, mapFragment, getActivity());
        }
    }

    public void hideFilter() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.remove(filterFragment);
        transaction.commit();


    }
}