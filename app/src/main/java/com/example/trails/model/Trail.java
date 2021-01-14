package com.example.trails.model;

import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trail {

    private String Id;
    private Characteristics characteristics;
    private ArrayList<Coordinates> coordinates;
    private String userId;
    private List<Pair<ImageData,LatLng>> imagesWithCoords;
    private List<Pair<String,Coordinates>> imagesCoords;
    private List<String> images;
    private float trailRating;
    //private List<Comment> listComments;

    public Trail(Characteristics characteristics, ArrayList<Coordinates> coordinates, String userId) {
        this.characteristics = characteristics;
        this.coordinates = coordinates;
        this.userId = userId;
        this.images = new ArrayList<>();
        this.imagesCoords = new ArrayList<>();
        //listComments = new ArrayList<>();
    }

    public Trail(){

    }

    @Exclude
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public List<Pair<ImageData,LatLng>> getImagesWithCoords() {
        return imagesWithCoords;
    }

    public void setImagesWithCoords(List<Pair<ImageData,LatLng>> imagesWithCoords) {
        this.imagesWithCoords = imagesWithCoords;
    }

    public List<Pair<String,Coordinates>> getImagesCoords() {
        return imagesCoords;
    }

    public void setImagesCoords(List<Pair<String,Coordinates>> imagesCoords) {
        this.imagesCoords = imagesCoords;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public float getTrailRating() {
        return trailRating;
    }

   /* public List<Comment> getListComments() {
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
    }*/

}

