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
import com.example.trails.ui.start.StartTrailAdapter;

public class FragmentTabDownloaded extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_downloaded, container, false);

        RecyclerView mRecyclerViewDownload = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view_downloaded);
        mRecyclerViewDownload.setHasFixedSize(true);

        StartTrailAdapter startTrailAdapter = new StartTrailAdapter(requireContext(), requireActivity(), LocalDB.getTrailsNameFromAssets(requireContext()));
        RecyclerView.LayoutManager mLayoutManagerDownload = new LinearLayoutManager(getActivity());
        mRecyclerViewDownload.setLayoutManager(mLayoutManagerDownload);
        mRecyclerViewDownload.setAdapter(startTrailAdapter);

        return rootView;
    }

}