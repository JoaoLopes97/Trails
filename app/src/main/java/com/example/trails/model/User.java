package com.example.trails.model;

import android.location.Location;

import java.util.Date;
import java.util.List;

public class User {

    private String name;
    private Date dateOfBirth;
    private String idUser;
    private String photo;
    private Location location; // Localização de residencia
    private String city;
    private String email;

    private List<Integer> favoriteRoutes;
    private List<Integer> downloadRoutes;

    public User(String name, Date dateOfBirth, Location location, String email, List<Integer> favoriteRoutes, List<Integer> downloadRoutes, String city) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.location = location;
        this.email = email;
        this.favoriteRoutes = favoriteRoutes;
        this.downloadRoutes = downloadRoutes;
        this.city = city;
    }

    public User(String name, String email, Date dateOfBirth, String city, String idUser, String photo) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.idUser = idUser;
        this.photo = photo;
    }

    public User() {
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public void addFavoriteTrail(int trailId) {
        this.favoriteRoutes.add(trailId);
    }

    public void removeFavoriteTrail(int trailId) {
        this.favoriteRoutes.remove(trailId);
    }

    public List<Integer> getDownloadRoutes() {
        return downloadRoutes;
    }

    public void setDownloadRoutes(List<Integer> downloadRoutes) {
        this.downloadRoutes = downloadRoutes;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
