package com.example.trails.ui.explore;

public class TrailCard {

    private String trailName;
    private String location;
    private float rating;
    private int reviews;
    private int photo;

    public TrailCard(String trailName, String location, float rating, int reviews,int photo) {
        this.trailName = trailName;
        this.location = location;
        this.rating = rating;
        this.reviews = reviews;
        this.photo = photo;
    }

    public String getTrailName() {
        return trailName;
    }

    public void setTrailName(String trailName) {
        this.trailName = trailName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}

