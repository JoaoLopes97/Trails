package com.example.trails.ui.myTrails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.SingletonCurrentUser;
import com.example.trails.model.Trail;
import com.example.trails.ui.explore.ExploreTrailAdapter;
import com.example.trails.ui.start.StartTrailAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import static com.example.trails.controller.DB.db;

public class FragmentTabMyTrails extends Fragment {

    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter<Trail, ExploreTrailAdapter.ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Query query;
    private FirestoreRecyclerOptions<Trail> options;
    private StartTrailAdapter startTrailAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_my_trails, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle bundle = this.getArguments();
        int position = bundle.getInt("position");

        changeTabs(position);

        return rootView;
    }

    public void changeTabs(int position){
        switch (position) {
            case 0: // My trails
                /*startTrailAdapter = new StartTrailAdapter(getActivity(), LocalDB.getTrailsNameFromAssets(getContext()));
                mRecyclerView.setAdapter(startTrailAdapter);*/
                break;
            case 1: // Download
                startTrailAdapter = new StartTrailAdapter(getContext(), getActivity(), LocalDB.getTrailsNameFromAssets(getContext()));
                mRecyclerView.setAdapter(startTrailAdapter);
                break;
            case 2: //Historico
                query = db.collection("trails").limit(50); //we can order by
                options = new FirestoreRecyclerOptions.Builder<Trail>().setQuery(query, Trail.class).build();
                mAdapter = new ExploreTrailAdapter(options, getActivity());
                mRecyclerView.setAdapter(mAdapter);
                break;
            case 3: // Favoritos
                //SingletonCurrentUser.getCurrentUserInstance().getFavoriteTrails();
                /*query = db.collection("trails"); //we can order by
                options = new FirestoreRecyclerOptions.Builder<Trail>().setQuery(query, Trail.class).build();
                mAdapter = new ExploreTrailAdapter(options, getActivity());
                mRecyclerView.setAdapter(mAdapter);*/
                break;
        }
    }

}