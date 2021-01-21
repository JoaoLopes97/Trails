package com.example.trails.ui.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.MainActivity;
import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Characteristics;
import com.example.trails.model.SingletonCurrentUser;
import com.example.trails.model.Trail;
import com.example.trails.model.User;
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
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Trail model) {
        model.setId(getSnapshots().getSnapshot(position).getId());
        holder.trail = model;
        holder.favoriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = SingletonCurrentUser.getCurrentUserInstance();
                if (holder.favoriteCheckBox.isChecked()) {
                    user.getFavoriteTrails().add(holder.trail.getId());
                } else {
                    user.removeFavoriteTrail(holder.trail.getId());
                }
                DB.updateUser(SingletonCurrentUser.getCurrentUserInstance());
            }
        });
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

        ImageButton favorites;

        CheckBox favoriteCheckBox;

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

            favoriteCheckBox = itemView.findViewById(R.id.favoriteCheckBoxCardView);
            favoriteCheckBox.setVisibility(View.VISIBLE);
        }

        boolean isFavoriteForUser() {
            String trailId = trail.getId();
            return SingletonCurrentUser.getCurrentUserInstance().getFavoriteTrails().contains(trailId);
        }

        void setDetails() {
            Characteristics ch = trail.getCharacteristics();
            txtTrailNameCard.setText(ch.getName());
            txtLocationCard.setText(ch.getLocation().getAddress());
            txtRatingCard.setRating(trail.getTrailRating());
            if (trail.getListReviews() != null)
                txtReviewsCard.setText(trail.getListReviews().size() + " reviews");
            String imageUrl = null;
            if (!trail.getImages().isEmpty()) {
                imageUrl = trail.getImages().get(0);
            } else if (!trail.getImagesCoords().isEmpty()) {
                imageUrl = trail.getImagesCoords().get(0).first;
            }

            if (isFavoriteForUser()) {
                favoriteCheckBox.setChecked(true);
            }

            if (imageUrl != null)
                DB.loadWithGlide(context, imageUrl, trailPhotoCard);
        }
    }
}
