package com.example.test.ui.film;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
import com.example.test.firebase.Movie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class FilmLoadFragment extends Fragment {

    public String json, imbdID, title, year, country, collection, uid
            ;
    public ImageView imageViewFilmLayout;
    public TextView textFilmLayoutTitle;
    public TextView textFilmLayoutRuntime;
    public TextView textFilmLayoutYear;
    public TextView textFilmLayoutPlot;
    public TextView textFilmLayoutGenre;
    public TextView textFilmLayoutCountry;
    public TextView textFilmLayoutDirector;
    public TextView textFilmLayoutRating;

    public View view;

    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    public boolean[] checkedItems;
    public List<String> selectedItems;
    public String[] listItems;

    public FilmLoadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            json = getArguments().getString("json");
            imbdID = getArguments().getString("ID");
            collection = getArguments().getString("collection");

            listItems = new String[]{"watched", "wishlist"};
            checkedItems  = new boolean[listItems.length];
            selectedItems = Arrays.asList(listItems);

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            country = jsonO.get("Country").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view -> addMovie());

        return view;
    }
    
    public void addMovie(){
        System.out.println("null");
        if(collection.equals("null")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Add to collection : ");

            builder.setMultiChoiceItems(listItems, checkedItems, (dialog, which, isChecked) -> {
                checkedItems[which] = isChecked;
                String currentItem = selectedItems.get(which);
            });
            builder.setPositiveButton("Done", (dialog, which) -> {
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        if (MainAuthentication.user == null) {
                            Toast.makeText(getActivity(), "No user is signed in", Toast.LENGTH_LONG).show();
                        } else {
                            Movie movie = new Movie(imbdID, "", "0", country, year);
                            DocumentReference documentReference = db.collection("users").document(uid).collection(selectedItems.get(i)).document(imbdID);
                            documentReference.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    documentReference.set(movie).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Movie saved", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Save erreur", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "Movie already saved!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            });

            builder.setNegativeButton("CANCEL", (dialog, which) -> {
            });

            builder.create();

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else {
            System.out.println("not null");
            Bundle bundle = new Bundle();
            bundle.putString("imdbID", imbdID);
            bundle.putString("title", title);
            bundle.putString("year", year);
            bundle.putString("country", country);
            bundle.putString("collection", collection);
            getActivity().runOnUiThread(() -> {
                Navigation.findNavController(view).navigate(R.id.action_nav_search_film_to_movie_add, bundle);
            });
        }
    }
}