package com.example.test.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
//import com.example.test.firebase.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button buttonAddMovie, buttonSetMovie;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        buttonAddMovie = root.findViewById(R.id.buttonAddMovieHome);
        buttonSetMovie = root.findViewById(R.id.buttonSetMovie);

        buttonAddMovie.setOnClickListener(this);
        buttonSetMovie.setOnClickListener(this);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddMovie:
                System.out.println("tram");
                addMovie();
                break;
            case R.id.buttonSetMovie:
                System.out.println("param");
                break;
        }
    }

    public void addMovie() {
        if (MainAuthentication.user == null) {
            System.out.println("No user is signed in"); // TODO
        } else {

            System.out.println("User is signed in");
            Movie movie = new Movie("imbdID", "note", "rating");
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(uid).collection("movies").add(movie);
            readDocs();
        }
    }
    public void readDocs(){
        System.out.println("---------------------------------------------------------------------");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // db.collection("users").document(uid).collection("movies").add(movie);

        db.collection("users").document(uid).collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("TAG", document.getId() + " => " + document.getString("imdbID"));
                }
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }
}