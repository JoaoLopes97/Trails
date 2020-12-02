package com.example.trails.ui.Details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;

import com.example.trails.R;

public class ImageFlipperFragment extends Fragment{
    ViewFlipper viewFlipper;

    int gallery_grid_Images[] = {R.drawable.sa1, R.drawable.sa2,
            R.drawable.sa3, R.drawable.sa4};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.image_slider_fragment, container, false);

        viewFlipper = root.findViewById(R.id.v_flipperId);

        for(int i=0; i<gallery_grid_Images.length; i++){
            setFlipperImage(gallery_grid_Images[i]);
        }

        return root;
    }

    private void setFlipperImage(int res) {
        ImageView image = new ImageView(getContext());
        image.setBackgroundResource(res);
        viewFlipper.addView(image);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);
    }
}