package com.example.test.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.test.FilmDisplayActivity;
import com.example.test.MovieAdapter;
import com.example.test.MovieData;
import com.example.test.OmdbApi.OmdbApiSearch;
import com.example.test.R;
import com.example.test.RecyclerViewClickInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    public HomeViewModel() {
    mText = new MutableLiveData<>();
    }
    public LiveData<String> getText() {
        return mText;
    }


public class SecondActivity extends AppCompatActivity implements RecyclerViewClickInterface {

    RecyclerView recyclerView;
    private ArrayList<MovieData> movieData;
    Intent intent;
    boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    MovieAdapter adapter;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_film);

        ImageButton btn = findViewById(R.id.searchButton);

        EditText txt = findViewById(R.id.editText);

        movieData = new ArrayList<>();

        adapter = new MovieAdapter(SecondActivity.this, movieData, SecondActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(SecondActivity.this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager layout = new LinearLayoutManager(SecondActivity.this);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        Log.d("testmsg", "juste pour voir");

        btn.setOnClickListener(v -> {
            title = txt.getText().toString();
            movieData.clear();
            Log.d("msgtest", "je teste pour verifier quelque chose");
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
                    Log.d("MSGSCROLL", "j'ai rescrollé jusqu'a la fin");
                    isScrolling = false;
                    if (totalItems%10 == 0) {
                        addMovieToAdapter(title, (totalItems/10)+1);
                    }
                }
            }
        });
    }

    private void addMovieToAdapter(String movieTitle, int page ) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ProgressDialog p = new ProgressDialog(this);
        p.setMessage("please wait");
        p.setIndeterminate(false);
        p.setCancelable(false);
        p.show();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Log.d("MSF", "this a thread message");
                OmdbApiSearch o = new OmdbApiSearch(movieTitle, getResources().getString(R.string.ApiKey));
                try {
                    JSONArray listJson = o.getMovies(page);

                    for (int i = 0; i<listJson.length(); i++) {
                        movieData.add(new MovieData((JSONObject) listJson.get(i)));
                    }

                    //adapter = new MovieAdapter(MainActivity.this, movieData, MainActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();

                            p.hide();
                            p.cancel();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Log.d("MSGTHREAD","clicked on: " + movieData.get(position).title);
        /*OmdbApiSearch o = new OmdbApiSearch(movieData.get(position).imdbID, "63f3e471");
        AsyncTaskOmdbFilm asyncTaskOmdbFilm = new AsyncTaskOmdbFilm();
        asyncTaskOmdbFilm.execute(o);*/
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                OmdbApiSearch o = new OmdbApiSearch(movieData.get(position).imdbID, getResources().getString(R.string.ApiKey));
                JSONObject json = o.getMovie();
                intent = new Intent(SecondActivity.this, FilmDisplayActivity.class);
                intent.putExtra("json", json.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });

            }
        });
    }
}
}