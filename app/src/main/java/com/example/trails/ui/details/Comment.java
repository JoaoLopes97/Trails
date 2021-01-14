package com.example.trails.ui.details;

public class Comment {

    private int rating;
    private String name;
    private String comentario;

    public Comment(int rating, String name, String comentario) {
        this.rating = rating;
        this.name = name;
        this.comentario = comentario;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
