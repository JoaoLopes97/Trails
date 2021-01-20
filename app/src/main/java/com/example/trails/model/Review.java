package com.example.trails.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Review implements Serializable {

    private String id;
    private String trailId;
    private String userId;
    private String comment;
    private float rating;

    public Review(String trailId, String userId, String comment, float rating) {
        this.trailId = trailId;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
    }

    public Review() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrailId() {
        return trailId;
    }

    public void setTrailId(String trailId) {
        this.trailId = trailId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
