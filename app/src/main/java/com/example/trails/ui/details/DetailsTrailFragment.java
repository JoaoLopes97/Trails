package com.example.trails.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.trails.R;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Trail;
import com.example.trails.ui.start.StartFragment;
import com.google.android.gms.maps.MapView;

import static com.example.trails.MainActivity.setFragment;

public class DetailsTrailFragment extends Fragment {

    private TextView titleWalk, description, distance, time_spent;
    private RatingBar ratingBar;
    private ImageButton downloadWalk;
    private ImageButton startWalk;
    public Trail trail;
    private ImageFlipperFragment imageFlipper;

    private LocalDB localDb;

    public DetailsTrailFragment(Trail trail) {
        this.trail = trail;
    }

    public DetailsTrailFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.details_fragment, container, false);


        titleWalk = root.findViewById(R.id.title_walk);
        ratingBar = root.findViewById(R.id.rating_bar);
        description = root.findViewById(R.id.description);
        distance = root.findViewById(R.id.distance);
        time_spent = root.findViewById(R.id.time_spent);
        downloadWalk = root.findViewById(R.id.DownloadButton);
        startWalk = root.findViewById(R.id.StartButton);

        MapDraw mapDraw = new MapDraw(trail);
        setFragment(R.id.map_fragment, mapDraw, getActivity());

        fillFragment();

        localDb = new LocalDB(getContext());

        setFragment(R.id.images_frag, new ImageFlipperFragment(trail.getImages(), trail.getImagesCoords()), getActivity());

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
        distance.setText(trailCh.getDistance() + " km"); //use resource string with placeholders
        time_spent.setText(trailCh.getTimeSpent() + "seconds");
    }
}
