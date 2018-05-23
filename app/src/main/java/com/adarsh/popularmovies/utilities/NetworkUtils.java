package com.adarsh.popularmovies.utilities;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String BASE_URL = "http://api.themoviedb.org/";

    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";

    private final static String API_KEY = "api_key";

    private final static String POPULAR_MOVIES_URL = "3/movie/popular";

    private final static String TOP_RATED_MOVIES_URL = "3/movie/top_rated";

    /**
     * Builds the Url based to fetch either the popular movies or the top rated movies
     *
     * @param type The type can be will be queried for.
     * @param api_key the API key
     * @return The URL to use to query the weather server.
     */
    public static URL buildMovieListUrl(String type, String api_key) {

        String URL_TYPE;

        if (type.equals(Constants.POPULAR_MOVIES)) {
            URL_TYPE = POPULAR_MOVIES_URL;
        } else {
            URL_TYPE = TOP_RATED_MOVIES_URL;
        }


        Uri uri = Uri.parse(BASE_URL + URL_TYPE).buildUpon()
                .appendQueryParameter(API_KEY, api_key)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getMoviePosterUrl(String posterUrl){
        return BASE_IMAGE_URL + posterUrl;
    }


}
