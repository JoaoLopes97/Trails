package com.example.trails.ui.start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.model.Trail;

import static com.example.trails.MainActivity.setFragment;

public class SaveTrailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.save_trail_fragment, container, false);

        Trail trail = (Trail) getArguments().getSerializable("trail");
        Fragment fragment = null;
        if (getArguments().getInt("type") == 0) {
            fragment = new InsertTrailFragment(trail);
        } else {
            fragment = new ReviewTrailFragment(trail);
        }
        setFragment(R.id.save_trail_fragment, fragment, getActivity());

        return root;
    }
}