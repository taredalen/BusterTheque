package com.example.test.ui.film;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
import com.example.test.firebase.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class MovieAdd extends Fragment implements View.OnClickListener {

    private String imbdID, title, year, stringNote;
    private Button buttonAddWatched, buttonAddWishlist;
    private TextView textMovieTitle, textMovieYear;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editTextNote;

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

        buttonAddWatched = view.findViewById(R.id.buttonAddWatched);
        buttonAddWishlist = view.findViewById(R.id.buttonAddWishlist);

        buttonAddWatched.setOnClickListener(this);
        buttonAddWishlist.setOnClickListener(this);

        editTextNote = view.findViewById(R.id.editTextNote);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddWatched:
                addMovieWatched();
                break;
            case R.id.buttonAddWishlist:
                addMovieWishlist();
                break;
        }
    }

    public void addMovieWatched() {
        if (MainAuthentication.user == null) {
            System.out.println("No user is signed in"); // TODO
        } else {
            System.out.println("User is signed in");
            System.out.println("test time : " + imbdID);

            stringNote = editTextNote.getText().toString().trim();
            if(stringNote.length() == 0 ) {
                stringNote = " ";
            }

            Movie movie = new Movie(imbdID, stringNote, "rating");
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference =  db.collection("users").document(uid).collection("watched").document(imbdID);
            documentReference.set(movie).addOnCompleteListener(task1 -> {
                if(task1.isSuccessful()){
                    System.out.println("added");
                }
                else {
                    System.out.println("erreur");
                }
            });
        }
    }

    public void addMovieWishlist() {
        if (MainAuthentication.user == null) {
            System.out.println("No user is signed in"); // TODO
        } else {
            System.out.println("User is signed in");
            System.out.println("test time : " + imbdID);

            stringNote = editTextNote.getText().toString().trim();
            if(stringNote.length() == 0 ) {
                stringNote = " ";
            }

            Movie movie = new Movie(imbdID, stringNote, "rating");
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference =  db.collection("users").document(uid).collection("wishlist").document(imbdID);
            documentReference.set(movie).addOnCompleteListener(task1 -> {
                if(task1.isSuccessful()){
                    System.out.println("added");
                }
                else {
                    System.out.println("erreur");
                }
            });
        }
    }
}
