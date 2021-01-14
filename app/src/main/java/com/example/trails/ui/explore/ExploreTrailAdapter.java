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

import com.bumptech.glide.Glide;
import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.Trail;
import com.example.trails.ui.details.DetailsTrailFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ExploreTrailAdapter extends FirestoreRecyclerAdapter<Trail, ExploreTrailAdapter.ViewHolder> {

    Context context;

    public ExploreTrailAdapter(@NonNull FirestoreRecyclerOptions<Trail> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Trail model) {
        model.setId(getSnapshots().get(position).getId());
        holder.trail = model;
        holder.setDetails();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        return new ViewHolder(view);
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
                    MainActivity.setFragment(R.id.details_frag, new DetailsTrailFragment(trail), (AppCompatActivity) context);
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
    }
}
