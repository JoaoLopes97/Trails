package com.example.trails.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.example.trails.controller.DB.db;
public class FilterFragment extends Fragment {

    private Button btnShow;
    private Button btnReset;
    private CheckBox checkBoxEasy;
    private CheckBox checkBoxMedium;
    private CheckBox checkBoxHard;
    private RatingBar ratingBar;

    private FirebaseFirestore fireStore;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.filter_fragment, container, false);

        btnShow = root.findViewById(R.id.filter_show);
        btnReset = root.findViewById(R.id.filter_reset);
        checkBoxEasy = root.findViewById(R.id.filter_dificulty_easy);
        checkBoxMedium = root.findViewById(R.id.filter_dificulty_medium);
        checkBoxHard = root.findViewById(R.id.filter_dificulty_hard);
        ratingBar = root.findViewById(R.id.filter_trail_rating);

        fireStore = FirebaseFirestore.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFilter();
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean easy = checkBoxEasy.isChecked();
                boolean medium = checkBoxMedium.isChecked();
                boolean hard = checkBoxHard.isChecked();

                float rating = ratingBar.getRating();

                ExploreFragment.filterData(easy, medium, hard, rating);
            }
        });
        return root;
    }

    private void resetFilter() {
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
