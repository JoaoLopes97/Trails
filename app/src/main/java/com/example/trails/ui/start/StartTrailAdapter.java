package com.example.trails.ui.start;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Trail;
import com.example.trails.ui.details.DetailsTrailFragment;

import java.util.ArrayList;
import java.util.List;

public class StartTrailAdapter extends RecyclerView.Adapter<StartTrailAdapter.ViewHolder>  {

    List<Trail> trails;
    Context context;

    public StartTrailAdapter(Context context, ArrayList<String> trailsNames) {
        this.context = context;
        trails = new ArrayList<>();
        for (String trailName : trailsNames){
            trails.add(LocalDB.getTrail(context,trailName));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trail trail = trails.get(position);
        holder.trail = trail;
        holder.setDetails();
    }

    @Override
    public int getItemCount() {
        return trails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTrailNameCard, txtLocationCard, txtReviewsCard;
        RatingBar txtRatingCard;
        ImageView trailPhotoCard;
        Trail trail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartFragment fragObj = new StartFragment(trail);
                    setFragment(R.id.nav_host_fragment, fragObj);
                }
            });

            txtTrailNameCard = itemView.findViewById(R.id.trail_name);
            txtLocationCard = itemView.findViewById(R.id.trail_location);
            txtRatingCard = itemView.findViewById(R.id.trail_rating);
            txtReviewsCard = itemView.findViewById(R.id.trail_reviews);
            trailPhotoCard = itemView.findViewById(R.id.trail_photo);
        }

        void setDetails() {
            Characteristics ch = trail.getCharacteristics();
            txtTrailNameCard.setText(ch.getName());
            txtLocationCard.setText(ch.getLocation().getAddress());
            txtRatingCard.setRating(trail.getTrailRating());
            //txtReviewsCard.setText(trail.getReviews() + " Reviews");
            DB.loadWithGlide(context, trail.getImages().get(0), trailPhotoCard); // TODO caso n haja ir buscar foto com coords
        }

        void setTrail(Trail trail) {
            this.trail = trail;
        }
    }

    private void setFragment(int layout, Fragment fragment) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
