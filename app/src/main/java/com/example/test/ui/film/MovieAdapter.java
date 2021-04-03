package com.example.test.ui.film;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import com.bumptech.glide.Glide;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    ArrayList<MovieData> movie;
    private final RecyclerViewClickInterface recyclerViewClickInterface;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewYear;
        ImageView imageViewPoster;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewYear = itemView.findViewById(R.id.textViewYear);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            itemView.setOnClickListener(v -> recyclerViewClickInterface.onItemClick(getAdapterPosition()));

        }

    }

    public MovieAdapter(Context context, ArrayList<MovieData> movie, RecyclerViewClickInterface recyclerViewClickInterface) {
        this.context = context;
        this.movie = movie;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        holder.textViewTitle.setText(movie.get(position).title);
        holder.textViewYear.setText(movie.get(position).year);
        //Glide.with(context).load(movie.get(position).poster)
        //        .placeholder(R.drawable.ic_launcher_background).into(holder.imageViewPoster);

        if (!movie.get(position).poster.equals("N/A")) {
            Glide.with(context).load(movie.get(position).poster)
                    .placeholder(R.drawable.ic_launcher_background).into(holder.imageViewPoster);
        } else {
            Glide.with(context).load(R.drawable.ic_launcher_background).into(holder.imageViewPoster);
        }

        /*if (!movie.get(position).poster.equals("N/A")) {
            Log.d("msg", movie.get(position).poster);
            Glide.with(context).load(movie.get(position).poster)
                    .placeholder(R.drawable.ic_launcher_background).into(holder.imageViewPoster);
        }*/

    }

    @Override
    public int getItemCount() {
        return movie.size();
    }
}


class MovieData {

    public String title;
    public String year;
    public String imdbID;
    public String poster;

    MovieData(JSONObject json) throws JSONException {
        prepareData(json);

    }

    private void prepareData(JSONObject json) throws JSONException {
        title = json.get("Title").toString();
        year = json.get("Year").toString();
        imdbID = json.get("imdbID").toString();
        poster = json.get("Poster").toString();
    }
}