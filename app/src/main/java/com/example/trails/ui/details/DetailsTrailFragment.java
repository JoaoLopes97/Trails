package com.example.trails.ui.details;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Trail;
import com.example.trails.ui.explore.MapFragment;
import com.example.trails.ui.explore.TrailAdapter;
import com.example.trails.ui.start.StartFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import static com.example.trails.MainActivity.db;
import static android.content.ContentValues.TAG;
import static com.example.trails.MainActivity.setFragment;

public class DetailsTrailFragment extends Fragment {

    private TextView titleWalk, description, distance, time_spent, location, terrain, difficulty;
    private LinearLayout commentsTitle, commentsContent, photosTitle, photosContent;
    private RatingBar ratingBar;
    private ImageButton downloadWalk;
    private Button startWalk;
    public static Trail trail;


    private LocalDB localDb;

    public DetailsTrailFragment(Trail trail) {
        this.trail = trail;
    }

    public DetailsTrailFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.details_fragment, container, false);

        MapFragment map = new MapFragment();

        titleWalk = root.findViewById(R.id.title_walk);
        ratingBar = root.findViewById(R.id.trail_rating);
        description = root.findViewById(R.id.trail_description);
        location = root.findViewById(R.id.trail_location);
        difficulty = root.findViewById(R.id.trail_difficulty);
        terrain = root.findViewById(R.id.trail_terrain);
        distance = root.findViewById(R.id.trail_distance);
        time_spent = root.findViewById(R.id.trail_time);
        downloadWalk = root.findViewById(R.id.DownloadButton);
        startWalk = root.findViewById(R.id.StartButton);

        commentsTitle = root.findViewById(R.id.commentsTitle);
        commentsContent = root.findViewById(R.id.commentsContent);

        photosTitle = root.findViewById(R.id.photosTitle);
        photosContent = root.findViewById(R.id.photosContent);

        commentsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentsContent.getVisibility() == View.GONE){
                    commentsContent.setVisibility(View.VISIBLE);
                }else{
                    commentsContent.setVisibility(View.GONE);
                }
            }
        });

        photosTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photosContent.getVisibility() == View.GONE){
                    photosContent.setVisibility(View.VISIBLE);
                }else{
                    photosContent.setVisibility(View.GONE);
                }
            }
        });

        setFragment(R.id.explore_details_frag, map, getActivity());

        fillFragment();

        localDb = new LocalDB(getContext());

        setFragment(R.id.images_frag, new ImageFlipperFragment(trail.getImages(),trail.getImagesCoords()), getActivity());

        startWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", trail.getId()); // colocar id do document
                StartFragment fragObj = new StartFragment();
                fragObj.setArguments(bundle);
                setFragment(R.id.nav_host_fragment, fragObj, getActivity());
            }
        });

        downloadWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                localDb.storeTrail(trail, trail.getId());
            }
        });

        return root;
    }

    private void fillFragment() {
        Characteristics trailCh = trail.getCharacteristics();
        titleWalk.setText(trailCh.getName());
        //ratingBar.setNumStars(5); //change when comments are added
        description.setText(trailCh.getDescription());
        location.setText(trailCh.getLocation().getAddress());
        terrain.setText(trailCh.getTerrainType().name());
        difficulty.setText(trailCh.getDifficulty().name());
        distance.setText(trailCh.getDistance() + " km"); //use resource string with placeholders
        time_spent.setText(trailCh.getTimeSpent() + "s");


    }
}
