package com.example.test.ui.film;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
import com.example.test.firebase.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;


public class FilmLoadFragment extends Fragment implements View.OnClickListener {

    public String json, imbdID, title, year;
    ImageView imageViewFilmLayout;
    TextView textFilmLayoutTitle;
    TextView textFilmLayoutRuntime;
    TextView textFilmLayoutYear;
    TextView textFilmLayoutPlot;
    TextView textFilmLayoutGenre;
    TextView textFilmLayoutCountry;
    TextView textFilmLayoutDirector;
    TextView textFilmLayoutRating;

    View view;

    private Button buttonAddMovie;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FilmLoadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            json = getArguments().getString("json");
            imbdID = getArguments().getString("ID");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.film_layout, container, false);
        imageViewFilmLayout = view.findViewById(R.id.imageViewFilmLayoutPoster);
        //textFilmLayoutCast = view.findViewById(R.id.textFilmLayoutCast);
        textFilmLayoutCountry = view.findViewById(R.id.textFilmLayoutCountry);
        textFilmLayoutDirector = view.findViewById(R.id.textFilmLayoutDirector);
        textFilmLayoutTitle = view.findViewById(R.id.textMovieTitle);
        textFilmLayoutRuntime = view.findViewById(R.id.textFilmLayoutRuntime);
        textFilmLayoutYear = view.findViewById(R.id.textFilmLayoutYear);
        textFilmLayoutPlot = view.findViewById(R.id.textFilmLayoutPlot);
        textFilmLayoutGenre = view.findViewById(R.id.textFilmLayoutGenre);
        //textFilmLayoutWriter = view.findViewById(R.id.textFilmLayoutWriter);
        //textFilmLayoutLanguage = view.findViewById(R.id.textFilmLayoutLanguage);
        textFilmLayoutRating = view.findViewById(R.id.textFilmLayoutRating);

        try {
            JSONObject jsonO = new JSONObject(json);
            if (!jsonO.get("Poster").toString().equals("N/A")) {
                Glide.with(this).load(jsonO.get("Poster").toString())
                        .placeholder(R.drawable.gradient).into(imageViewFilmLayout);
            }
            textFilmLayoutDirector.setText(jsonO.get("Director").toString());
            textFilmLayoutGenre.setText(jsonO.get("Genre").toString());
            textFilmLayoutYear.setText(jsonO.get("Year").toString());
            textFilmLayoutPlot.setText(jsonO.get("Plot").toString());
            textFilmLayoutCountry.setText(jsonO.get("Country").toString());
            textFilmLayoutRuntime.setText(jsonO.get("Runtime").toString());
            textFilmLayoutTitle.setText(jsonO.get("Title").toString());
            textFilmLayoutRating.setText(jsonO.get("imdbRating").toString());

            //textFilmLayoutCast.setText(jsonO.get("Actors").toString());
            //textFilmLayoutLanguage.setText(jsonO.get("Language").toString());
            //textFilmLayoutWriter.setText(jsonO.get("Writer").toString());

            title = jsonO.get("Title").toString();
            year = jsonO.get("Year").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        buttonAddMovie = (Button) view.findViewById(R.id.buttonAddMovie);
        buttonAddMovie.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAddMovie) {
            Bundle bundle = new Bundle();
            bundle.putString("imdbID", imbdID);
            bundle.putString("title", title);
            bundle.putString("year", year);
            getActivity().runOnUiThread(() -> {
                Navigation.findNavController(view).navigate(R.id.action_nav_search_film_to_movie_add, bundle);
            });
            //addMovie();
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