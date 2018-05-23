package com.adarsh.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adarsh.popularmovies.utilities.Constants;
import com.adarsh.popularmovies.utilities.MovieData;
import com.adarsh.popularmovies.utilities.NetworkUtils;
import com.adarsh.popularmovies.utilities.OpenMovieJsonUtils;
import com.adarsh.popularmovies.utilities.UIHelper;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MovieAdapter
        .MovieAdapterClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private ProgressBar mProgressBar;

    private TextView mErrorView;

    private boolean mPopularSelected = true;

    private ArrayList<MovieData> popularMovieList = new ArrayList<>();

    private ArrayList<MovieData> topRatedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);
        mProgressBar = findViewById(R.id.pb_loading_indicator);
        mErrorView = findViewById(R.id.tv_error_message_display);

        int numColumns = UIHelper.calculateNoOfColumns(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numColumns));
        mRecyclerView.hasFixedSize();
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            mPopularSelected = savedInstanceState.getBoolean(Constants.IS_POPULAR_SELECTED, true);
            if (savedInstanceState.containsKey(Constants.POPULAR_MOVIES)) {
                popularMovieList = savedInstanceState.getParcelableArrayList(Constants
                        .POPULAR_MOVIES);
            }
            if (savedInstanceState.containsKey(Constants.TOP_RATED)) {
                topRatedList = savedInstanceState.getParcelableArrayList(Constants.TOP_RATED);
            }
        }

        loadData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.IS_POPULAR_SELECTED, mPopularSelected);
        outState.putParcelableArrayList(Constants.POPULAR_MOVIES, popularMovieList);
        outState.putParcelableArrayList(Constants.TOP_RATED, topRatedList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.movie_type, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_top_rated:
                if (mPopularSelected) {
                    mPopularSelected = false;
                    loadData();
                }
                break;
            case R.id.action_popular:
                if (!mPopularSelected) {
                    mPopularSelected = true;
                    loadData();
                }
                break;
        }
        return true;
    }

    @Override
    public void onItemClicked(MovieData item) {
        Intent mIntent = new Intent(this, DetailActivity.class);
        mIntent.putExtra(Constants.MOVIE_DATA, item);
        startActivity(mIntent);
    }


    private void loadData() {
        if (isOnline()) {
            if (mPopularSelected) {
                setTitle(R.string.popular_movies);
                loadMovieData(Constants.POPULAR_MOVIES);
            } else {
                setTitle(R.string.top_rated);
                loadMovieData(Constants.TOP_RATED);
            }
        } else {
            showNoInternetDialog();
        }
    }

    // AsyncTask and call it loadMovieData
    private void loadMovieData(String type) {
        if (type.equals(Constants.POPULAR_MOVIES) && popularMovieList.size() > 0) {
            showMovieDataView();
            mMovieAdapter.setData(popularMovieList);
        } else if (type.equals(Constants.TOP_RATED) && topRatedList.size() > 0) {
            showMovieDataView();
            mMovieAdapter.setData(topRatedList);
        } else {
            LoadMovieDataTask loadMovieDataTask = new LoadMovieDataTask();
            loadMovieDataTask.execute(type);
        }
    }

    private void showErrorData() {
        mErrorView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showMovieDataView() {
        mErrorView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void showNoInternetDialog() {
        showErrorData();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.no_internet);
        builder.setMessage(R.string.please_turn_on);
        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

        return false;

    }

    class LoadMovieDataTask extends AsyncTask<String, Void, List<MovieData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieData> doInBackground(String... strings) {
            String movie_type = strings[0];
            URL url = NetworkUtils.buildMovieListUrl(movie_type, getString(R.string.api_key));
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<MovieData> data = null;
            if (response != null && !response.isEmpty()) {
                try {
                    data = OpenMovieJsonUtils.getMovieData(response);
                    if (movie_type.equals(Constants.POPULAR_MOVIES)) {
                        popularMovieList.clear();
                        popularMovieList.addAll(data);
                    } else {
                        topRatedList.clear();
                        topRatedList.addAll(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(List<MovieData> response) {
            super.onPostExecute(response);
            mProgressBar.setVisibility(View.INVISIBLE);
            if (response != null) {
                showMovieDataView();
                mMovieAdapter.setData(response);
            } else {
                showErrorData();
            }
        }
    }
}
