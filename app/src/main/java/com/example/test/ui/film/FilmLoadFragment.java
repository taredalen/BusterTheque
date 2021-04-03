package com.example.test.ui.film;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.test.R;

import org.json.JSONException;
import org.json.JSONObject;


public class FilmLoadFragment extends Fragment {

    String json;
    ImageView imageViewFilmLayout;
    TextView textFilmLayoutTitle;
    TextView textFilmLayoutRuntime;
    TextView textFilmLayoutYear;
    TextView textFilmLayoutCast;
    TextView textFilmLayoutPlot;
    TextView textFilmLayoutGenre;
    TextView textFilmLayoutCountry;
    TextView textFilmLayoutDirector;
    TextView textFilmLayoutWriter;
    TextView textFilmLayoutLanguage;

    public FilmLoadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            json = getArguments().getString("json");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.film_layout, container, false);
        imageViewFilmLayout = view.findViewById(R.id.imageViewFilmLayoutPoster);
        textFilmLayoutCast = view.findViewById(R.id.textFilmLayoutCast);
        textFilmLayoutCountry = view.findViewById(R.id.textFilmLayoutCountry);
        textFilmLayoutDirector = view.findViewById(R.id.textFilmLayoutDirector);
        textFilmLayoutTitle = view.findViewById(R.id.textFilmLayoutTitle);
        textFilmLayoutRuntime = view.findViewById(R.id.textFilmLayoutRuntime);
        textFilmLayoutYear = view.findViewById(R.id.textFilmLayoutYear);
        textFilmLayoutPlot = view.findViewById(R.id.textFilmLayoutPlot);
        textFilmLayoutGenre = view.findViewById(R.id.textFilmLayoutGenre);
        textFilmLayoutWriter = view.findViewById(R.id.textFilmLayoutWriter);
        textFilmLayoutLanguage = view.findViewById(R.id.textFilmLayoutLanguage);

        try {
            JSONObject jsonO = new JSONObject(json);
            if (!jsonO.get("Poster").toString().equals("N/A")) {
                Glide.with(this).load(jsonO.get("Poster").toString())
                        .placeholder(R.drawable.ic_launcher_background).into(imageViewFilmLayout);
            }
            textFilmLayoutDirector.setText(jsonO.get("Director").toString());
            textFilmLayoutCast.setText(jsonO.get("Actors").toString());
            textFilmLayoutGenre.setText(jsonO.get("Genre").toString());
            textFilmLayoutLanguage.setText(jsonO.get("Language").toString());
            textFilmLayoutYear.setText(jsonO.get("Year").toString());
            textFilmLayoutPlot.setText(jsonO.get("Plot").toString());
            textFilmLayoutCountry.setText(jsonO.get("Country").toString());
            textFilmLayoutRuntime.setText(jsonO.get("Runtime").toString());
            textFilmLayoutWriter.setText(jsonO.get("Writer").toString());
            textFilmLayoutTitle.setText(jsonO.get("Title").toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }



        return view;
    }
}