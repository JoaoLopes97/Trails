package com.example.trails.ui.explore;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.model.Coordinates;
import com.example.trails.model.Trail;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.trails.MainActivity.setFragment;
import static com.example.trails.controller.DB.db;

public class ExploreFragment extends Fragment {

    private static FilterFragment filterFragment;
    private static Fragment thisFragment;
    private FloatingActionButton viewMode;
    private static ListFragment listFragment;
    private MapFragment mapFragment;
    private static View filterLayout;
    public static HashMap<String, Coordinates> trails = new HashMap<>();

    private boolean viewMode_Map = false; // true if viewMode is Map, false if viewMode is List.
    private static boolean showingFilter = false;

    private static FragmentActivity activity;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.explore_fragment, container, false);

        thisFragment = this;

        filterFragment = new FilterFragment();

        listFragment = new ListFragment();

        activity = (FragmentActivity)root.getContext();


        setFragment(R.id.explore_frag, listFragment, getActivity());
        viewMode = root.findViewById(R.id.view_mode);
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewMode();
            }
        });

        final FloatingActionButton filterBtn = root.findViewById(R.id.filter);

        filterLayout = root.findViewById(R.id.filter_frag);

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

            setFragment(R.id.explore_frag, listFragment, getActivity());
        } else {
            viewMode.setImageResource(R.drawable.ic_baseline_list_24);
            viewMode_Map = true;

            mapFragment = new MapFragment();
            setFragment(R.id.explore_frag, mapFragment, getActivity());
        }
    }

    public static void hideFilter() {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        transaction.remove(filterFragment);
        transaction.commit();
    }

    public static void filterData(boolean terrainEasy, boolean terrainMedium, boolean terrainHard, boolean easy, boolean medium, boolean hard, float rating) {
        hideFilter();
        filterLayout.setClickable(false);
        showingFilter = false;



        Query query = db.collection("trails");

//        query = query.whereGreaterThanOrEqualTo("characteristics.distance", distanceLow);
//        query = query.whereLessThanOrEqualTo("characteristics.distance", distanceHigh);

        List<String> terrenoFields = new ArrayList<String>();

        if (terrainEasy) {
            terrenoFields.add("Baixo");
        }
        if (terrainMedium) {
            terrenoFields.add("Medio");
        }
        if (terrainHard) {
            terrenoFields.add("Alto");
        }

        if (terrainEasy || terrainMedium || terrainHard) {
            query = query.whereIn("characteristics.terrainType", terrenoFields);
        }

        List<String> fields = new ArrayList<String>();

        if (easy) {
            fields.add("Facil");
        }
        if (medium) {
            fields.add("Mediano");
        }
        if (hard) {
            fields.add("Dificil");
        }

        if (easy || medium || hard) {
            query = query.whereIn("characteristics.difficulty", fields);
        }

        if (rating != 0) {
            query = query.whereGreaterThanOrEqualTo("trailRating", rating);
        }

        FirestoreRecyclerOptions<Trail> options = new FirestoreRecyclerOptions.Builder<Trail>().setQuery(query, Trail.class).build();

        listFragment.setQuery(query);


        return;
    }
}