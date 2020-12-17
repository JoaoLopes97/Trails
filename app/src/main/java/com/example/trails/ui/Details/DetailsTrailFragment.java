package com.example.trails.ui.Details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.ui.explore.MapFragment;
import com.example.trails.ui.start.StartFragment;

public class DetailsTrailFragment extends Fragment {

    private TextView tittleWalk;
    private RatingBar ratingBar;
    private ImageButton downloadWalk;
    private ImageButton startWalk;

    private ImageFlipperFragment imageFlipper;

    private DB db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.details_fragment, container, false);

        MapFragment map = new MapFragment();

        setButtons(root);
        setFragment(R.id.explore_details_frag, map);

        db = new DB();

        startWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", "nZPmn3x9wj9VjBH9d4Og");
                StartFragment fragObj = new StartFragment();
                fragObj.setArguments(bundle);
                setFragment(R.id.nav_host_fragment,fragObj);
            }
        });

        downloadWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // db.insertData();
            }
        });

        return root;
    }

    public void setButtons(View root){
        tittleWalk = root.findViewById(R.id.TittleWalk);
        ratingBar = root.findViewById(R.id.RatingBar);
        downloadWalk = root.findViewById(R.id.DownloadButton);
        startWalk = root.findViewById(R.id.StartButton);
    }

    private void setFragment(int layout,Fragment fragment){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
