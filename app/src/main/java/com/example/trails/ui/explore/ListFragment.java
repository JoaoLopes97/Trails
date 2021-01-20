package com.example.trails.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.model.Trail;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import static com.example.trails.controller.DB.db;

public class ListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private FirestoreRecyclerAdapter<Trail, ExploreTrailAdapter.ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Query query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_fragment, container, false);

        query = db.collection("trails").limit(50);
        FirestoreRecyclerOptions<Trail> options = new FirestoreRecyclerOptions.Builder<Trail>().setQuery(this.query, Trail.class).build();

        mRecyclerView = root.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ExploreTrailAdapter(options, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public void setQuery(Query queryEdit) {
        mAdapter.updateOptions(new FirestoreRecyclerOptions.Builder<Trail>().setQuery(queryEdit, Trail.class).build());

    }


}
