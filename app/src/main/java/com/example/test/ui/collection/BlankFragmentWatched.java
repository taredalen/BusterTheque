package com.example.test.ui.collection;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.OmdbApi.OmdbApiSearch;
import com.example.test.R;
import com.example.test.ui.film.MovieAdapter;
import com.example.test.ui.film.MovieData;
import com.example.test.ui.film.RecyclerViewClickInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BlankFragmentWatched extends Fragment implements RecyclerViewClickInterface {

    public RecyclerView recyclerView;
    public ArrayList<MovieData> movieData;
    public MovieAdapter adapter;
    public View view;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public BlankFragmentWatched() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blank_watched, container, false);

        //String[] movieId = {"tt0060196", "tt1979388"};

        movieData = new ArrayList<>();
        adapter = new MovieAdapter(getActivity(), movieData, this);

        recyclerView = view.findViewById(R.id.recyclerViewDisplayFilmCollection);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

        readDocs();

        return view;
    }

    public void readDocs(){
        ArrayList<String> list = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("DOCS", document.getId() + " => " + document.getString("imdbID"));
                    list.add(document.getString("imdbID"));
                }
                System.out.println("---------------------------------------------------------------------");
                System.out.println(list);
                setFilmById(list);
            } else {
                Log.d("DOCS", "Error getting documents: ", task.getException());
            }
        });
    }

    public void setFilmById(ArrayList<String> movieId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("please wait");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                progressDialog.hide();
                progressDialog.cancel();
            });
        });
    }

    @Override
    public void onItemClick(int position) {
    }
}