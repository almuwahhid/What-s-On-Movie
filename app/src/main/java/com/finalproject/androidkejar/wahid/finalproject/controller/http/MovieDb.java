package com.finalproject.androidkejar.wahid.finalproject.controller.http;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gueone on 5/9/2016.
 */
public class MovieDb{
    public final String URL = "http://api.themoviedb.org/3/movie";
    public final String API_KEY = "5f18fc01159ece17d0a5bbb38b49ac28";
    public String CATEGORY;

    public String getCATEGORY() {
        return CATEGORY;
    }

    public void setCATEGORY(String CATEGORY) {
        this.CATEGORY = CATEGORY;
    }
}
