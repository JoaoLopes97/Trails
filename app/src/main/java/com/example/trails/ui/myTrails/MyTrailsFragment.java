package com.example.trails.ui.myTrails;

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

public class MyTrailsFragment extends Fragment {

    private MyTrailsViewModel myTrailsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myTrailsViewModel =
                ViewModelProviders.of(this).get(MyTrailsViewModel.class);
        View root = inflater.inflate(R.layout.my_trails_fragment, container, false);
        final TextView textView = root.findViewById(R.id.text_my_trails);
        myTrailsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}