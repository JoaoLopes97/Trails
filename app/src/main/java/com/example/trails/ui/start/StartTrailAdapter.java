package com.example.trails.ui.start;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.controller.LocalDB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Trail;

import java.util.ArrayList;
import java.util.List;

public class StartTrailAdapter extends RecyclerView.Adapter<StartTrailAdapter.ViewHolder> {

    List<Trail> trails;
    Context context;
    Activity activity;

    public StartTrailAdapter(Context context, Activity activity, ArrayList<String> trailsNames) {
        this.context = context;
        this.activity = activity;
        trails = new ArrayList<>();
        for (String trailName : trailsNames) {
            trails.add(LocalDB.getTrail(context, trailName));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Trail trail = trails.get(position);
        holder.trail = trail;
        holder.setDetails();
        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                LocalDB.deleteTrial(context, trails.get(position).getId());
                trails.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), trails.size());
            }
        });
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
        ImageButton trash;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putSerializable("trail", trail);
                    Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.nav_start, b);
                }
            });

            txtTrailNameCard = itemView.findViewById(R.id.trail_name);
            txtLocationCard = itemView.findViewById(R.id.trail_location);
            txtRatingCard = itemView.findViewById(R.id.trail_rating);
            txtReviewsCard = itemView.findViewById(R.id.trail_reviews);
            trailPhotoCard = itemView.findViewById(R.id.trail_photo);
            trash = itemView.findViewById(R.id.trash);
            trash.setVisibility(View.VISIBLE);
        }

        void setDetails() {
            Characteristics ch = trail.getCharacteristics();
            txtTrailNameCard.setText(ch.getName());
            txtLocationCard.setText(ch.getLocation().getAddress());
            txtRatingCard.setRating(trail.getTrailRating());
            //txtReviewsCard.setText(trail.getReviews() + " Reviews");
            DB.loadWithGlide(context, trail.getImages().get(0), trailPhotoCard); // TODO caso n haja ir buscar foto com coords
        }
    }
}
