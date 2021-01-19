package com.example.trails.ui.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.SingletonCurrentUser;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
import com.example.trails.ui.explore.MapFragment;
import com.example.trails.ui.start.StartFragment;

import static com.example.trails.MainActivity.setFragment;

public class DetailsTrailFragment extends Fragment {

    private TextView titleWalk, description, distance, time_spent, location, terrain, difficulty;
    private LinearLayout commentsTitle, commentsContent, photosTitle, photosContent;
    private RatingBar ratingBar;
    private ImageButton downloadWalk;
    private Button startWalk;
    public Trail trail;
    private ImageFlipperFragment imageFlipper;
    private CheckBox favoriteCheckBox;

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
        favoriteCheckBox = root.findViewById(R.id.favoriteCheckBox);

        MapDraw mapDraw = new MapDraw(trail);
        setFragment(R.id.map_fragment, mapDraw, getActivity());

        commentsTitle = root.findViewById(R.id.commentsTitle);
        commentsContent = root.findViewById(R.id.commentsContent);

        photosTitle = root.findViewById(R.id.photosTitle);
        photosContent = root.findViewById(R.id.photosContent);

        commentsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentsContent.getVisibility() == View.GONE) {
                    commentsContent.setVisibility(View.VISIBLE);
                } else {
                    commentsContent.setVisibility(View.GONE);
                }
            }
        });

        photosTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photosContent.getVisibility() == View.GONE) {
                    photosContent.setVisibility(View.VISIBLE);
                } else {
                    photosContent.setVisibility(View.GONE);
                }
            }
        });

        fillFragment();

        setFragment(R.id.images_frag, new ImageFlipperFragment(trail.getImages(), trail.getImagesCoords()), getActivity());

        startWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("trail", trail);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.nav_start, b);
            }
        });

        downloadWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LocalDB.storeTrail(getContext(), trail, trail.getId());
            }
        });

        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    User user = SingletonCurrentUser.getCurrentUserInstance();
                    user.getFavoriteTrails().add(trail.getId());
                    DB.updateUser(SingletonCurrentUser.getCurrentUserInstance());
                }else{
                    User user = SingletonCurrentUser.getCurrentUserInstance();
                    user.removeFavoriteTrail(trail.getId());
                    DB.updateUser(SingletonCurrentUser.getCurrentUserInstance());
                }
            }
        });

        return root;
    }

    public boolean isFavoriteForUser(){
        String trailId = trail.getId();
        return SingletonCurrentUser.getCurrentUserInstance().getFavoriteTrails().contains(trailId);
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
        time_spent.setText(trailCh.getTimeSpent() + "seconds");

        if(isFavoriteForUser()){
            favoriteCheckBox.setChecked(true);
        }

    }
}
