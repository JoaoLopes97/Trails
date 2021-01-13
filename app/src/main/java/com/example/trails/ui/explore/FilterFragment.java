package com.example.trails.ui.explore;

import android.media.Rating;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.trails.R;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

public class FilterFragment extends Fragment {

    private Button btnShow;
    private Button btnReset;
    private RadioGroup radioGroup;
    private RangeSlider rangeSlider;
    private CheckBox checkBoxEasy;
    private CheckBox checkBoxMedium;
    private CheckBox checkBoxHard;
    private RatingBar ratingBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.filter_fragment, container, false);

        rangeSlider = root.findViewById(R.id.filter_trail_distance_slider);
        btnShow = root.findViewById(R.id.filter_show);
        btnReset = root.findViewById(R.id.filter_reset);
        radioGroup = root.findViewById(R.id.filter_trail_type);
        checkBoxEasy = root.findViewById(R.id.filter_dificulty_easy);
        checkBoxMedium = root.findViewById(R.id.filter_dificulty_medium);
        checkBoxHard = root.findViewById(R.id.filter_dificulty_hard);
        ratingBar = root.findViewById(R.id.filter_trail_rating);


        rangeSlider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return value + " km";
            }
        });
        rangeSlider.setValues(1.0f, 5.0f);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilter();
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return root;
    }

    private void resetFilter() {
        radioGroup.clearCheck();
        rangeSlider.setValues(1.0f, 5.0f);
        ratingBar.setRating((float) 0.0);

        if (checkBoxEasy.isChecked()) {
            checkBoxEasy.toggle();
        }
        if (checkBoxMedium.isChecked()) {
            checkBoxMedium.toggle();
        }
        if (checkBoxHard.isChecked()) {
            checkBoxHard.toggle();
        }
    }
}
