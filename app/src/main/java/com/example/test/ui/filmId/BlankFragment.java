package com.example.test.ui.filmId;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.test.OmdbApi.OmdbApiSearch;
import com.example.test.R;
import com.example.test.ui.film.MovieAdapter;
import com.example.test.ui.film.MovieData;
import com.example.test.ui.film.RecyclerViewClickInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BlankFragment extends Fragment implements RecyclerViewClickInterface {

    RecyclerView recyclerView;
    private ArrayList<MovieData> movieData;
    boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    MovieAdapter adapter;
    String title;
    View view;

    public BlankFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_blank, container, false);

        String[] movieId = {"tt0060196", "tt1979388"};

        movieData = new ArrayList<>();

        adapter = new MovieAdapter(getActivity(), movieData, this);
        recyclerView = view.findViewById(R.id.recyclerViewDisplayFilmCollection);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

        try {
            setFilmById(movieId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }
    public void setFilmById(String[] movieId) throws JSONException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ProgressDialog p = new ProgressDialog(getActivity());
        p.setMessage("please wait");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();
        executor.execute(() -> {
            OmdbApiSearch o = new OmdbApiSearch("", "63f3e471");

            for (String s : movieId) {
                o.setTitle(s);
                try {
                    movieData.add(new MovieData(o.getMovie()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            getActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                p.hide();
                p.cancel();
            });
        });


    }

    @Override
    public void onItemClick(int position) {

    }
}