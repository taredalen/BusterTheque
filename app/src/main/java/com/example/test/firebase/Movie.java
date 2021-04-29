package com.example.test.firebase;

public class Movie {
    
    public String imdbID, note, rating, country;

    public Movie(){
    }

    public Movie(String imdbID, String note, String rating, String country){
        this.imdbID = imdbID;
        this.note = note;
        this.rating = rating;
        this.country = country;
    }
}



