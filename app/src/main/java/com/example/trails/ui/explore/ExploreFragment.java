package com.example.trails.ui.explore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.model.Coordinates;
import com.example.trails.model.Trail;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.example.trails.MainActivity.setFragment;
import static com.example.trails.controller.DB.db;

public class ExploreFragment extends Fragment {

    private static FilterFragment filterFragment;
    private FloatingActionButton viewMode;
    private FloatingActionButton filterBtn;
    private static ListFragment listFragment;
    private static MapFragment mapFragment;
    private static View filterLayout;
    public static HashMap<String, Coordinates> trails = new HashMap<>();

    private static boolean viewMode_Map = false; // true if viewMode is Map, false if viewMode is List.
    private static boolean showingFilter = false;

    private static FragmentActivity activity;

    private EditText editTextSearch;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.explore_fragment, container, false);


        filterFragment = new FilterFragment();

        listFragment = new ListFragment();

        mapFragment = new MapFragment();

        activity = (FragmentActivity) root.getContext();


        setFragment(R.id.explore_frag, listFragment, getActivity());
        viewMode = root.findViewById(R.id.view_mode);
        viewMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewMode();
            }
        });

        filterBtn = root.findViewById(R.id.filter);

        filterLayout = root.findViewById(R.id.filter_frag);

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showingFilter) {
                    setFragment(R.id.filter_frag, filterFragment, getActivity());
                    filterLayout.setClickable(true);
                    showingFilter = true;
                } else {
                    hideFilter();
                    filterLayout.setClickable(false);
                    showingFilter = false;
                }
            }
        });

        editTextSearch = root.findViewById(R.id.search);
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    filterDataSearch(editTextSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });


        return root;
    }

    private void changeViewMode() {

        if (viewMode_Map) {
            viewMode.setImageResource(R.drawable.ic_baseline_map_24);
            viewMode_Map = false;

            setFragment(R.id.explore_frag, listFragment, getActivity());
        } else {
            viewMode.setImageResource(R.drawable.ic_baseline_list_24);
            viewMode_Map = true;

            setFragment(R.id.explore_frag, mapFragment, getActivity());
        }
    }

    public static void hideFilter() {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();

        transaction.remove(filterFragment);
        transaction.commit();
    }

    public static void filterData(boolean easy, boolean medium, boolean hard, float rating) {
        hideFilter();
        filterLayout.setClickable(false);
        showingFilter = false;

        Query query = db.collection("trails");


        List<String> fields = new ArrayList<String>();

        if (easy) {
            fields.add("Facil");
        }
        if (medium) {
            fields.add("Mediano");
        }
        if (hard) {
            fields.add("Dificil");
        }

        if (easy || medium || hard) {
            query = query.whereIn("characteristics.difficulty", fields);
        }

        if (rating != 0) {
            query = query.whereGreaterThanOrEqualTo("trailRating", rating);
        }

        if (viewMode_Map) {
            mapFragment.setQuery(query);
        } else {
            listFragment.setQuery(query);
        }
    }

    public void filterDataSearch(String text) {
        Query query;
        if(text.isEmpty()) {
            query = db.collection("trails");
        } else {
            query = db.collection("trails").whereIn("characteristics.name", Collections.singletonList(text));

        }

        if (viewMode_Map) {
            mapFragment.setQuery(query);
        } else {
            listFragment.setQuery(query);
        }
    }
}