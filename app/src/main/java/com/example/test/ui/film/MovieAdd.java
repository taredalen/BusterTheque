package com.example.test.ui.film;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
import com.example.test.firebase.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieAdd extends Fragment implements View.OnClickListener {

    private String imbdID, title, year;
    private Button buttonAddWatched, buttonAddWishlist;
    private TextView textMovieTitle, textMovieYear;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MovieAdd() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imbdID = getArguments().getString("imdbID");
            title = getArguments().getString("title");
            year = getArguments().getString("year");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_movie_add, container, false);

        textMovieTitle = view.findViewById(R.id.textMovieTitle);
        textMovieYear = view.findViewById(R.id.textMovieYear);

        textMovieTitle.setText(title);
        textMovieYear.setText(year);

        buttonAddWatched = view.findViewById(R.id.buttonAddMovieHome);
        buttonAddWishlist = view.findViewById(R.id.buttonSetMovie);

        buttonAddWatched.setOnClickListener(this);
        buttonAddWishlist.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddWatched:
                System.out.println("tram");
                addMovie();
                break;
            case R.id.buttonAddWishlist:
                System.out.println("param");
                break;
        }
    }

    public void addMovie() {
        if (MainAuthentication.user == null) {
            System.out.println("No user is signed in"); // TODO
        } else {
            System.out.println("User is signed in");
            System.out.println("test time : " + imbdID);

            Movie movie = new Movie(imbdID, "note", "rating");
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(uid).collection("movies").add(movie);
        }
    }
}
