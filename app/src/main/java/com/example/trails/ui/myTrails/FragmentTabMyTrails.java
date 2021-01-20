package com.example.trails.ui.myTrails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.model.SingletonCurrentUser;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
import com.example.trails.ui.explore.ExploreTrailAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.List;

import static com.example.trails.controller.DB.db;

public class FragmentTabMyTrails extends Fragment {

    private RecyclerView mRecyclerView;
    private ExploreTrailAdapter exploreTrailAdapter;
    private final int position;

    public FragmentTabMyTrails(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_my_trails, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        changeTabs(position);
        return rootView;
    }

    public void changeTabs(int position) {
        User user = SingletonCurrentUser.getCurrentUserInstance();
        FirestoreRecyclerOptions<Trail> options;
        Query query;
        switch (position) {
            case 0: // My trails
                query = db.collection("trails").whereEqualTo("userId", user.getIdUser()).limit(50);
                options = new FirestoreRecyclerOptions.Builder<Trail>().setQuery(query, Trail.class).build();
                exploreTrailAdapter = new ExploreTrailAdapter(options, requireContext());
                mRecyclerView.setAdapter(exploreTrailAdapter);
                break;
            case 2: //Historico
                List<String> madeTrails = user.getMadeTrails();
                if (!madeTrails.isEmpty()) {
                    query = db.collection("trails").whereIn("id", madeTrails); //we can order by
                    options = new FirestoreRecyclerOptions.Builder<Trail>().setQuery(query, Trail.class).build();
                    exploreTrailAdapter = new ExploreTrailAdapter(options, requireContext());
                    mRecyclerView.setAdapter(exploreTrailAdapter);
                }
                break;
            case 3: // Favoritos
                List<String> favouriteTrails = user.getFavoriteTrails();
                if (!favouriteTrails.isEmpty()) {
                    query = db.collection("trails").whereIn("id", favouriteTrails);
                    options = new FirestoreRecyclerOptions.Builder<Trail>().setQuery(query, Trail.class).build();
                    exploreTrailAdapter = new ExploreTrailAdapter(options, requireContext());
                    mRecyclerView.setAdapter(exploreTrailAdapter);
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (exploreTrailAdapter != null)
            exploreTrailAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (exploreTrailAdapter != null)
            exploreTrailAdapter.stopListening();
    }

}