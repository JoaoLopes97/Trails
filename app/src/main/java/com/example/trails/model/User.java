package com.example.trails.model;

import android.location.Location;

import java.util.Date;
import java.util.List;

public class User {

    private String name;
    private Date dateOfBirth;
    private Location location; // Localização de residencia

    //private String image; https://www.youtube.com/watch?v=7puuTDSf3pk

    private String email;

    private List<Routes> favoriteRoutes;
    private List<Routes> downloadRoutes;

    public User(String name, Date dateOfBirth, Location location, String email, List<Routes> favoriteRoutes, List<Routes> downloadRoutes) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
        this.email = email;
        this.favoriteRoutes = favoriteRoutes;
        this.downloadRoutes = downloadRoutes;
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

    public List<Routes> getFavoriteRoutes() {
        return favoriteRoutes;
    }

    public void setFavoriteRoutes(List<Routes> favoriteRoutes) {
        this.favoriteRoutes = favoriteRoutes;
    }

    public List<Routes> getDownloadRoutes() {
        return downloadRoutes;
    }

    public void setDownloadRoutes(List<Routes> downloadRoutes) {
        this.downloadRoutes = downloadRoutes;
    }
}
