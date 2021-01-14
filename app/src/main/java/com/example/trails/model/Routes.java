package com.example.trails.model;

import java.util.List;

public class Routes {

    private List<Coordinates> listCoordinates;
    private List<Comment> listComments;

    private Characteristics characteristics;
    private float routeRating;

    public Routes(List<Coordinates> listCoordinates, List<Comment> listComments, Characteristics characteristics, float routeRating) {
        this.listCoordinates = listCoordinates;
        this.listComments = listComments;
        this.characteristics = characteristics;
        this.routeRating = routeRating;
    }

    public List<Coordinates> getListCoordinates() {
        return listCoordinates;
    }

    public void setListCoordinates(List<Coordinates> listCoordinates) {
        this.listCoordinates = listCoordinates;
    }

    public List<Comment> getListComments() {
        return listComments;
    }

    public void setListComments(List<Comment> listComments) {
        this.listComments = listComments;
    }

    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Characteristics characteristics) {
        this.characteristics = characteristics;
    }

    public float getRouteRating() {
        return routeRating;
    }

    public void setRouteRating(float routeRating) {
        this.routeRating = routeRating;
    }
}
