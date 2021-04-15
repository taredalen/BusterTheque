package com.example.test.OmdbApi;

import android.util.Log;
import org.json.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OmdbApiSearch {

    private static final String URL_S = "http://www.omdbapi.com/?apikey=KEY&s=TITLE&type=movie&page=PAGENUMBER";
    private static final String URL_T = "http://www.omdbapi.com/?apikey=KEY&i=TITLE&type=movie";

    private String title;
    private final String apiKey;

    public void setTitle(String title) {
        this.title = title;
    }

    private JSONObject httpGetRequest(String Url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(Url).build();

        try {
            Response response = client.newCall(request).execute();
            String filmResponse = response.body().string();
            Log.d("MSG_OKHTTPTEST", filmResponse);
            return new JSONObject(filmResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray getMovies(int page) throws JSONException {
        String request = URL_S.replaceAll("TITLE", title);
        request = request.replaceAll("KEY", apiKey);
        request = request.replaceAll("PAGENUMBER", String.valueOf(page));

        JSONObject jsonFilm = httpGetRequest(request);


        if (jsonFilm == null) {
            jsonFilm = new JSONObject("{\"Search\":[{\"Title\":\"The Good, the Bad and the Ugly\"" +
                    ",\"Year\":\"1966\",\"imdbID\":\"tt0060196\",\"Type\":\"movie\"" +
                    ",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BOTQ5NDI3MTI4MF5BMl5BanBnXkFtZTgwNDQ4ODE5MDE@._V1_SX300.jpg\"}]" +
                    ",\"totalResults\":\"917\",\"Response\":\"True\"}");
            //return null;

        } else {
            Log.d("msg", jsonFilm.toString());
            return jsonFilm.getJSONArray("Search");
        }
        Log.d("msg", jsonFilm.toString());
        return jsonFilm.getJSONArray("Search");

    }

    public JSONObject getMovie() throws JSONException {
        String request = URL_T.replaceAll("TITLE", title);
        request = request.replaceAll("KEY", apiKey);
        JSONObject x = httpGetRequest(request);

        /*JSONObject jobject = new JSONObject("{\"Title\":\"The Good, the Bad and the Ugly\"" +
                ",\"Year\":\"1966\",\"imdbID\":\"tt0060196\",\"Type\":\"movie\"" +
                ",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BOTQ5NDI3MTI4MF5BMl5BanBnXkFtZTgwNDQ4ODE5MDE@._V1_SX300.jpg\"}");*/

        return x;

    }

    public OmdbApiSearch(String title, String apiKey) {
        this.title = title.trim();
        this.apiKey = apiKey;
    }
}
