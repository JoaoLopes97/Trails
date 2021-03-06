package com.example.trails.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {

    private String name;
    private Date dateOfBirth;
    private String idUser;
    private String photo;
    private Address address;
    private String email;

    private List<String> favoriteTrails = new ArrayList<>();
    private List<String> madeTrails = new ArrayList<>();

    private double kmTotal;
    private long timeInTrails;
    private int finishedTrails;

    public User(String name, String email, Date dateOfBirth, Address address, String idUser, String photo) {
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.idUser = idUser;
        this.photo = photo;
        this.address = address;
        this.kmTotal = 0;
        this.finishedTrails = 0;
        this.timeInTrails = 0;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFavoriteTrails() {
        return favoriteTrails;
    }

    public void setFavoriteTrails(List<String> favoriteTrails) {
        this.favoriteTrails = favoriteTrails;
    }

    public void addFavoriteTrail(String trailId) {
        this.favoriteTrails.add(trailId);
    }

    public void removeFavoriteTrail(String trailId) {
        this.favoriteTrails.remove(trailId);
    }

    public void addMadeTrail(String trailId){
        this.madeTrails.add(trailId);
    }

    @Exclude
    public String getCity() {
        return address.getAddress();
    }

    public List<String> getMadeTrails() {
        return madeTrails;
    }

    public void setMadeTrails(List<String> madeTrails) {
        this.madeTrails = madeTrails;
    }

    public void addMadeTrails(String trailId) {
        this.madeTrails.add(trailId);
    }

    public void removeMadeTrails(String trailId) {
        this.madeTrails.remove(trailId);
    }

    public double getKmTotal() {
        return kmTotal;
    }

    public void setKmTotal(double kmTotal) {
        this.kmTotal += kmTotal;
    }

    public long getTimeInTrails() {
        return timeInTrails;
    }

    public void setTimeInTrails(long time) {
        this.timeInTrails += time;
    }

    public int getFinishedTrails() {
        return finishedTrails;
    }

    public void setFinishedTrails() {
        this.finishedTrails = finishedTrails + 1;
    }
}
