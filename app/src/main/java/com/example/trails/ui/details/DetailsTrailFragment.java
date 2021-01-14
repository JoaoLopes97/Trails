package com.example.trails.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trails.R;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Trail;
import com.example.trails.ui.explore.MapFragment;
import com.example.trails.ui.start.StartFragment;

public class DetailsTrailFragment extends Fragment {

    private TextView titleWalk, description, distance, time_spent;
    private RatingBar ratingBar;
    private ImageButton downloadWalk;
    private ImageButton startWalk;
    public static Trail trail;

    private ImageFlipperFragment imageFlipper;

    public DetailsTrailFragment(Trail trail) {
        this.trail = trail;
    }

    public DetailsTrailFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.details_fragment, container, false);

        MapFragment map = new MapFragment();

        titleWalk = root.findViewById(R.id.title_walk);
        ratingBar = root.findViewById(R.id.rating_bar);
        description = root.findViewById(R.id.description);
        distance = root.findViewById(R.id.distance);
        time_spent = root.findViewById(R.id.time_spent);
        downloadWalk = root.findViewById(R.id.DownloadButton);
        startWalk = root.findViewById(R.id.StartButton);
        setFragment(R.id.explore_details_frag, map);

        fillFragment();

        startWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartFragment fragObj = new StartFragment(trail);
                setFragment(R.id.nav_host_fragment, fragObj);
            }
        });

        downloadWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocalDB.storeTrail(getContext(),trail, trail.getId());
            }
        });

        return root;
    }

    private void fillFragment() {
        Characteristics trailCh = trail.getCharacteristics();
        titleWalk.setText(trailCh.getName());
        //ratingBar.setNumStars(5); //change when comments are added
        description.setText(trailCh.getDescription());
        distance.setText(trailCh.getDistance() + " km"); //use resource string with placeholders
        time_spent.setText(trailCh.getTimeSpent() + "seconds");


    }

    private void setFragment(int layout, Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
