package com.example.test.ui.film;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.OmdbApi.OmdbApiSearch;
import com.example.test.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchFilmFragment extends Fragment implements RecyclerViewClickInterface {

    RecyclerView recyclerView;
    private ArrayList<MovieData> movieData;
    boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    MovieAdapter adapter;
    String title;
    View view;

    public SearchFilmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.search_film, container, false);
        ImageButton btn = view.findViewById(R.id.searchButton);

        EditText txt = view.findViewById(R.id.editText);

        movieData = new ArrayList<>();

        adapter = new MovieAdapter(getActivity(), movieData, this);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);

        btn.setOnClickListener(v -> {
            title = txt.getText().toString();
            movieData.clear();
            addMovieToAdapter(title, 1);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL && movieData.size() != 0) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = layout.getChildCount();
                totalItems = layout.getItemCount();
                scrollOutItems = layout.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    if (totalItems%10==0) {
                        addMovieToAdapter(title, (totalItems/10)+1);
                    }
                }
            }
        });

        return view;
    }

    private void addMovieToAdapter(String movieTitle, int page ) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ProgressDialog p = new ProgressDialog(getActivity());
        p.setMessage("please wait");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();
        executor.execute(() -> {
            OmdbApiSearch o = new OmdbApiSearch(movieTitle, "63f3e471");
            try {
                JSONArray listJson = o.getMovies(page);

                if (listJson != null) {
                    for (int i = 0; i<listJson.length(); i++) {
                        movieData.add(new MovieData((JSONObject) listJson.get(i)));
                    }
                }
                //adapter = new MovieAdapter(MainActivity.this, movieData, MainActivity.this);
                getActivity().runOnUiThread(() -> {
                    if (listJson == null) {
                        Toast.makeText(getActivity(), "no movie found", Toast.LENGTH_LONG).show();
                    }

                    adapter.notifyDataSetChanged();
                    p.hide();
                    p.cancel();
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
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

            getActivity().runOnUiThread(() -> {
                Navigation.findNavController(view).navigate(R.id.action_nav_search_film_to_nav_film_display, bundle);
            });

        });
    }
}