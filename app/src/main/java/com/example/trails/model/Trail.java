package com.example.trails.model;

import android.media.Image;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trail {

    private Characteristics characteristics;
    private ArrayList<Coordinates> coordinates;
    private int userId;
    private HashMap<Image, Coordinates> images; // rever
    private float trailRating;
    private List<Comment> listComments;

    public Trail(Characteristics characteristics, ArrayList<Coordinates> coordinates, int userId, HashMap<Image, Coordinates> images) {
        this.characteristics = characteristics;
        this.coordinates = coordinates;
        this.userId = userId;
        this.images = images;
    }

    public Trail(){

    }
    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Characteristics characteristics) {
        this.characteristics = characteristics;
    }

    public ArrayList<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public HashMap<Image, Coordinates> getImages() {
        return images;
    }

    public void setImages(HashMap<Image, Coordinates> images) {
        this.images = images;
    }

    public float getTrailRating() {
        return trailRating;
    }

    public List<Comment> getListComments() {
        return listComments;
    }

    public void addComment(Comment comment){
        if(comment != null){
            listComments.add(comment);
            updateRating();
        }
    }

    public void updateRating() {
        int numComments = 0;
        float rating = 0;
        for (Comment c : listComments) {
            numComments++;
            rating += c.getRating();
        }
        trailRating = rating / numComments;
    }

}

