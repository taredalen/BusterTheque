package com.example.test.ui.film;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieData {

    public String title;
    public String year;
    public String imdbID;
    public String poster;

    public MovieData(JSONObject json) throws JSONException {
        prepareData(json);

    }

    private void prepareData(JSONObject json) throws JSONException {
        title = json.get("Title").toString();
        year = json.get("Year").toString();
        imdbID = json.get("imdbID").toString();
        poster = json.get("Poster").toString();
    }
}
