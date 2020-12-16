package com.example.trails.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    private String name;
    private Date dateOfBirth;
    private Location location; // Localização de residencia

    //private String image; https://www.youtube.com/watch?v=7puuTDSf3pk

    private String email;

    private List<Integer> favoriteRoutes;
    private List<Integer> downloadRoutes;

    public User(String name, Date dateOfBirth, Location location, String email, List<Integer> favoriteRoutes, List<Integer> downloadRoutes) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
        this.email = email;
        this.favoriteRoutes = favoriteRoutes;
        this.downloadRoutes = downloadRoutes;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(List<Integer> favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    public void addFavoriteTrail(int trailId){
        this.favoriteRoutes.add(trailId);
    }

    public void removeFavoriteTrail(int trailId){
        this.favoriteRoutes.remove(trailId);
    }

    public List<Integer> getDownloadRoutes() {
        return downloadRoutes;
    }

    public void setDownloadRoutes(List<Integer> downloadRoutes) {
        this.downloadRoutes = downloadRoutes;
    }
}
