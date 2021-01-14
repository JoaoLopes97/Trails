package com.example.trails.model;

public class Characteristics {

    private String name;
    private String description;

    private TrailDifficulty difficulty;
    private TerrainType terrainType;

    private float distance;
    private float timeSpent;

    private Address location;

    // mais caracteristicas
    // validações de null devem ser feitas na interface??

    public Characteristics(String name, String description, TrailDifficulty difficulty, TerrainType terrainType, float distance, float timeSpent) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.terrainType = terrainType;
        this.distance = distance;
        this.timeSpent = timeSpent;
    }

    public Characteristics() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TrailDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(TrailDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(float timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Address getLocation() {
        return location;
    }

    public void setLocation(Address location) {
        this.location = location;
    }
}
