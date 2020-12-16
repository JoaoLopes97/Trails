package com.example.trails.model;

public class Characteristics {

    private String name;
    private String description;

    private float difficulty;
    private TerrainType terrainType;

    private float distance;
    private float timeSpent;

    //private location

    // mais caracteristicas
    // validações de null devem ser feitas na interface??

    public Characteristics(String name, String description, float difficulty, TerrainType terrainType, float distance,float timeSpent) {
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.terrainType = terrainType;
        this.distance = distance;
        this.timeSpent = timeSpent;
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

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
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
}
