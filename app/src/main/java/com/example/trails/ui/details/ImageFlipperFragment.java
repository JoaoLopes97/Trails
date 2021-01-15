package com.example.trails.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Coordinates;
import com.example.trails.model.Pair;

import java.util.List;

public class ImageFlipperFragment extends Fragment {
    ViewFlipper viewFlipper;

    public List<String> images;

    public ImageFlipperFragment(List<String> images, List<Pair<String, Coordinates>> imagesCoords) {
        this.images = images;
        for (Pair<String, Coordinates> image : imagesCoords) {
            this.images.add(image.first);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.image_slider_fragment, container, false);

        viewFlipper = root.findViewById(R.id.v_flipperId);

        for (String image : images) {
            setFlipperImage(image);
        }

        return root;
    }

    private void setFlipperImage(String imageUrl) {
        ImageView image = new ImageView(getContext());
        DB.loadWithGlide(getContext(), imageUrl, image);
        //image.setBackgroundResource(res);
        viewFlipper.addView(image);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);
    }
}