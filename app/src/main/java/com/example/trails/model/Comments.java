package com.example.trails.model;

public class Comments {

    private User user;
    private String comment;
    private float rating;

    public Comments(User user, String comment, float rating) {
        this.user = user;
        this.comment = comment;
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
