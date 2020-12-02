package com.example.trails.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    //private ExploreViewModel exploreViewModel;
    private FloatingActionButton viewMode;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TrailCard> trailCards;
    private TextView title;
    boolean check_ScrollingUp = false;

    private boolean viewMode_Map = true; // true if viewMode is Map, false if viewMode is List.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //exploreViewModel =  ViewModelProviders.of(this).get(ExploreViewModel.class);
        View root = inflater.inflate(R.layout.explore_fragment, container, false);

        InitializeCardView(root);

        viewMode = root.findViewById(R.id.view_mode);
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewMode();
            }
        });

        //title = root.findViewById(R.id.app_name);
        return root;
    }

    private void InitializeCardView(View view) {
        mRecyclerView = view.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;
            MainActivity main = (MainActivity) getActivity();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int currentFirstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
                if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                    title.setVisibility(View.INVISIBLE);
                    //main.getSupportActionBar().hide();
                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                    title.setVisibility(View.VISIBLE);
                    //main.getSupportActionBar().show();
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });

        /*mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                MainActivity main = (MainActivity) getActivity();
                if (dy > 0) {
                    // Scrolling up
                    if (check_ScrollingUp) {
                        main.getSupportActionBar().hide();
                        //view1.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.trans_downwards));
                        check_ScrollingUp = false;
                    }

                } else {
                    // User scrolls down
                    if (!check_ScrollingUp) {
                        main.getSupportActionBar().show();
                        //view1.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.trans_upwards));
                        check_ScrollingUp = true;

                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });*/
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        trailCards = new ArrayList<>();
        mAdapter = new RecyclerViewAdapter(getActivity(), trailCards);
        mRecyclerView.setAdapter(mAdapter);

        CreateTrailsCards();
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
        mAdapter.notifyDataSetChanged();
    }

    private void changeViewMode() {
        if (viewMode_Map) {
            viewMode.setImageResource(R.drawable.ic_baseline_list_24);
            viewMode_Map = false;
        } else {
            viewMode.setImageResource(R.drawable.ic_baseline_map_24);
            viewMode_Map = true;
        }
    }
}