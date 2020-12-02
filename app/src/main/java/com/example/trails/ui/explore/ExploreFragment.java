package com.example.trails.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.trails.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    //private ExploreViewModel exploreViewModel;
    private FloatingActionButton viewMode;
    private ListFragment listFragment;
    private MapFragment mapFragment;
    public static ArrayList<TrailCard> trailCards = new ArrayList<>();

    private boolean viewMode_Map = false; // true if viewMode is Map, false if viewMode is List.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.explore_fragment, container, false);

        setFragment(new ListFragment());
        viewMode = root.findViewById(R.id.view_mode);
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewMode();
            }
        });

        CreateTrailsCards();
        return root;
    }

    private void CreateTrailsCards() {
        TrailCard trail = new TrailCard("Trilho da Arrabida", "Arrabida,Setubal,Portugal", (float) 4.5, 57, R.mipmap.portinho_arrabida_1);
        trailCards.add(trail);
        trail = new TrailCard("Trilho da Arrabida 2", "Arrabida,Setubal,Portugal", (float) 2, 358, R.mipmap.portinho_arrabida_2);
        trailCards.add(trail);
        trail = new TrailCard("Trilho da Arrabida 3", "Arrabida,Setubal,Portugal", (float) 3.5, 170, R.mipmap.portinho_arrabida_1);
        trailCards.add(trail);
        trail = new TrailCard("Trilho da Arrabida 4", "Arrabida,Setubal,Portugal", (float) 1, 3, R.mipmap.portinho_arrabida_2);
        trailCards.add(trail);
        trail = new TrailCard("Trilho da Arrabida 5", "Arrabida,Setubal,Portugal", (float) 5, 10, R.mipmap.portinho_arrabida_1);
        trailCards.add(trail);
    }

    private void changeViewMode() {

        if (viewMode_Map) {
            viewMode.setImageResource(R.drawable.ic_baseline_map_24);
            viewMode_Map = false;

            listFragment = new ListFragment();
            setFragment(listFragment);
        } else {
            viewMode.setImageResource(R.drawable.ic_baseline_list_24);
            viewMode_Map = true;

            mapFragment = new MapFragment();
            setFragment(mapFragment);
        }
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.explore_frag, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}