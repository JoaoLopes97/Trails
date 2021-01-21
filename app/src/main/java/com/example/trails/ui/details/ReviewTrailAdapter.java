package com.example.trails.ui.details;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trails.R;
import com.example.trails.controller.DB;
import com.example.trails.model.Review;
import com.example.trails.model.SingletonCurrentUser;
import com.example.trails.model.User;

import java.util.List;

public class ReviewTrailAdapter extends RecyclerView.Adapter<com.example.trails.ui.details.ReviewTrailAdapter.ViewHolder> {

    List<Review> reviews;
    Context context;
    Activity activity;

    public ReviewTrailAdapter(Context context, Activity activity, List<Review> reviews) {
        this.context = context;
        this.activity = activity;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewTrailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card_view, parent, false);

        return new ReviewTrailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewTrailAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.setDetails(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameCard, reviewCommentCard;
        RatingBar ratingBarCard;
        ImageView reviewPhotoCard;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            userNameCard = itemView.findViewById(R.id.name_user);
            reviewCommentCard = itemView.findViewById(R.id.review_comment);
            ratingBarCard = itemView.findViewById(R.id.review_rating);
            reviewPhotoCard = itemView.findViewById(R.id.user_image);
        }

        void setDetails(Review review) {
            DB.getUserDB(review.getUserId(),userNameCard,reviewPhotoCard,context);
            User user = SingletonCurrentUser.getCurrentUserInstance();
            reviewCommentCard.setText(review.getComment());
            ratingBarCard.setRating(review.getRating());
        }
    }
}
