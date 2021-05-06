package com.example.test.firebase;

public class Movie {
    
    public String imdbID, note, rating, country, year;

    public Movie(){
    }

    public Movie(String imdbID, String note, String rating, String country, String year){
        this.imdbID = imdbID;
        this.note = note;
        this.rating = rating;
        this.country = country;
        this.year = year;
    }
}



