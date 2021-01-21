package com.example.trails.ui.start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.model.ImageData;
import com.example.trails.model.Pair;
import com.example.trails.model.Trail;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static com.example.trails.MainActivity.setFragment;

public class SaveTrailFragment extends Fragment {

    private List<Pair<ImageData, LatLng>> imagesWithCoords;
    public SaveTrailFragment(List<Pair<ImageData, LatLng>> imagesWithCoords) {
        this.imagesWithCoords = imagesWithCoords;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.save_trail_fragment, container, false);

        Trail trail = (Trail) getArguments().getSerializable("trail");
        Fragment fragment = null;
        if (getArguments().getInt("type") == 0) {
            fragment = new InsertTrailFragment(trail,imagesWithCoords);
        } else {
            fragment = new ReviewTrailFragment(trail,imagesWithCoords);
        }
        setFragment(R.id.save_trail_fragment, fragment, getActivity());

        return root;
    }
}