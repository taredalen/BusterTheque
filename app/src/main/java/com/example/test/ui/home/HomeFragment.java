package com.example.test.ui.home;

import android.app.ProgressDialog;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.OmdbApi.OmdbApiSearch;
import com.example.test.R;
import com.example.test.firebase.MainAuthentication;
import com.example.test.firebase.Movie;
import com.example.test.ui.film.MovieAdapter;
import com.example.test.ui.film.MovieData;
import com.example.test.ui.film.RecyclerViewClickInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeFragment extends Fragment implements RecyclerViewClickInterface {

    public RecyclerView recyclerView;
    public ArrayList<MovieData> movieData;
    public MovieAdapter adapter;
    public View view;
    public final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blank_watched, container, false);

        movieData = new ArrayList<>();
        adapter = new MovieAdapter(getActivity(), movieData, this);

        recyclerView = view.findViewById(R.id.recyclerViewDisplayFilmCollection);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(adapter);

        readDocs();

        return view;
    }

    public void readDocs(){
        ArrayList<String> list = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("watched").get().addOnCompleteListener(task -> {
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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String id = movieData.get(position).imdbID;
            OmdbApiSearch o = new OmdbApiSearch(movieData.get(position).imdbID, "63f3e471");
            JSONObject json = null;
            try {
                json = o.getMovie();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Bundle bundle = new Bundle();
            bundle.putString("json", json.toString());
            bundle.putString("ID", id);
            bundle.putString("collection", "watched");
            getActivity().runOnUiThread(() -> {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_nav_film_display, bundle);
            });
        });
    }
}