package com.example.trails.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.trails.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExploreFragment extends Fragment {

    private ExploreViewModel exploreViewModel;
    private FloatingActionButton viewMode;
    private boolean viewMode_Map = true; // true if viewMode is Map, false if viewMode is List.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        exploreViewModel =
                ViewModelProviders.of(this).get(ExploreViewModel.class);
        View root = inflater.inflate(R.layout.explore_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_explore);
        exploreViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        viewMode = root.findViewById(R.id.view_mode);
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewMode();
            }
        });
        return root;
    }

    private void changeViewMode(){
        if(viewMode_Map){
            viewMode.setImageResource(R.drawable.ic_baseline_list_24);
            viewMode_Map = false;
        }else {
            viewMode.setImageResource(R.drawable.ic_baseline_map_24);
            viewMode_Map = true;
        }
    }
}