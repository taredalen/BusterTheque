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
}