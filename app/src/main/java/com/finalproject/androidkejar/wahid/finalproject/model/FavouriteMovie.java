package com.finalproject.androidkejar.wahid.finalproject.model;

/**
 * Created by gueone on 5/10/2016.
 */
public class FavouriteMovie {
    int id;
    String idMovie;
    boolean status;
    String title;
    String path;

    public FavouriteMovie() {
    }

    public int getId() {
        return id;
    }

    public String getIdMovie() {
        return idMovie;
    }

    public boolean isStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdMovie(String idMovie) {
        this.idMovie = idMovie;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
