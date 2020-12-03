package com.example.trails.ui.explore;

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
import com.example.trails.ui.Details.DetailsTrailFragment;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderNew> {

    Context context;
    private ArrayList<TrailCard> trails;

    public RecyclerViewAdapter(Context context, ArrayList<TrailCard> trails) {
        this.context = context;
        this.trails = trails;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setFragment(R.id.details_frag, new DetailsTrailFragment());
        }
    };

    @NonNull
    @Override
    public ViewHolderNew onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        view.setOnClickListener(mOnClickListener);

        return  new ViewHolderNew(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNew holder, int position) {
        TrailCard trailCard = trails.get(position);
        holder.setDetails(trailCard);
    }

    @Override
    public int getItemCount() {
        return trails.size();
    }

    private void setFragment(int layout, Fragment fragment){
        FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.replace(layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    class ViewHolderNew extends RecyclerView.ViewHolder {

        TextView txtTrailNameCard,txtLocationCard,txtReviewsCard;
        RatingBar txtRatingCard;
        ImageView trailPhotoCard;

        public ViewHolderNew(@NonNull View itemView) {
            super(itemView);
            txtTrailNameCard = itemView.findViewById(R.id.trail_name);
            txtLocationCard = itemView.findViewById(R.id.trail_location);
            txtRatingCard = itemView.findViewById(R.id.trail_rating);
            txtReviewsCard = itemView.findViewById(R.id.trail_reviews);
            trailPhotoCard = itemView.findViewById(R.id.trail_photo);
        }

        void setDetails(TrailCard trail){
            txtTrailNameCard.setText(trail.getTrailName());
            txtLocationCard.setText(trail.getLocation());
            txtRatingCard.setRating(trail.getRating());
            txtReviewsCard.setText(trail.getReviews() + " Reviews");
            trailPhotoCard.setImageResource(trail.getPhoto());
        }
    }
}
