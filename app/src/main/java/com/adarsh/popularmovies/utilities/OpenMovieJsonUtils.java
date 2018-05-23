package com.adarsh.popularmovies.utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OpenMovieJsonUtils {

    private static final String RESULTS = "results";

    private static final String VOTE_COUNT = "vote_count";

    private static final String ID = "id";

    private static final String VIDEO = "video";

    private static final String VOTE_AVERAGE = "vote_average";

    private static final String TITLE = "title";

    private static final String POPULARITY = "popularity";

    private static final String POSTER_PATH = "poster_path";

    private static final String ORIGINAL_LANGUAGE = "original_language";

    private static final String ORIGINAL_TITLE = "original_title";

    private static final String GENRE_IDS = "genre_ids";

    private static final String BACKDROP_PATH = "backdrop_path";

    private static final String ADULT = "adult";

    private static final String OVERVIEW = "overview";

    private static final String RELEASE_DATE = "release_date";

    private static final String TAG = OpenMovieJsonUtils.class.getSimpleName();

    public static List<MovieData> getMovieData(String response) throws
            JSONException {

        if (response == null) {
            return null;
        }

        JSONObject movieJson;

        try {
            movieJson = new JSONObject(response);
        } catch (JSONException e) {
            throw e;
        }

        return getMovieList(movieJson);

    }


    private static List<MovieData> getMovieList(JSONObject movieJson) {
        List<MovieData> movieList = new ArrayList<>();
        if (movieJson.has(RESULTS)) {
            try {
                JSONArray moviesArray = movieJson.getJSONArray(RESULTS);
                for (int i = 0; i < moviesArray.length(); i++) {
                    movieList.add(getMovie(moviesArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                Log.d(TAG, "moviesArray parsing failed" + e.getMessage());
            }
        }
        return movieList;
    }

    private static MovieData getMovie(JSONObject jsonObject) {
        return new MovieData(getInt(VOTE_COUNT, jsonObject), getInt(ID, jsonObject), getBoolean
                (VIDEO, jsonObject), getDouble(VOTE_AVERAGE, jsonObject),
                getString(TITLE, jsonObject), getDouble(POPULARITY, jsonObject), getString
                (POSTER_PATH, jsonObject), getString(ORIGINAL_LANGUAGE, jsonObject),
                getString(ORIGINAL_TITLE, jsonObject), getIntArray(GENRE_IDS, jsonObject),
                getString(BACKDROP_PATH, jsonObject), getBoolean(ADULT, jsonObject),
                getString(OVERVIEW, jsonObject), getString(RELEASE_DATE, jsonObject));
    }

    private static double getDouble(String name, JSONObject jsonObject) {
        double result = 0;
        if (jsonObject != null && jsonObject.has(name)) {
            try {
                result = jsonObject.getDouble(name);
            } catch (JSONException e) {
                Log.d(TAG, name + " parsing failed" + e.getMessage());
            }
        }
        return result;
    }

    private static List<Integer> getIntArray(String name, JSONObject jsonObject) {
        List<Integer> list = new ArrayList<>();
        if (jsonObject != null && jsonObject.has(name)) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray(name);
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(jsonArray.getInt(i));
                }
            } catch (JSONException e) {
                Log.d(TAG, name + " parsing failed" + e.getMessage());
            }
        }
        return list;
    }

    private static String getString(String name, JSONObject jsonObject) {
        String result = "";
        if (jsonObject != null && jsonObject.has(name)) {
            try {
                result = jsonObject.getString(name);
            } catch (JSONException e) {
                Log.d(TAG, name + " parsing failed" + e.getMessage());
            }
        }
        return result;
    }

    private static int getInt(String name, JSONObject jsonObject) {
        int result = 0;
        if (jsonObject != null && jsonObject.has(name)) {
            try {
                result = jsonObject.getInt(name);
            } catch (JSONException e) {
                Log.d(TAG, name + " parsing failed" + e.getMessage());
            }
        }
        return result;
    }

    private static boolean getBoolean(String name, JSONObject jsonObject) {
        boolean result = false;
        if (jsonObject != null && jsonObject.has(name)) {
            try {
                result = jsonObject.getBoolean(name);
            } catch (JSONException e) {
                Log.d(TAG, name + " parsing failed" + e.getMessage());
            }
        }
        return result;
    }

}
