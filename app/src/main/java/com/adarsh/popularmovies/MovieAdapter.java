package com.adarsh.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adarsh.popularmovies.utilities.MovieData;
import com.adarsh.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final List<MovieData> mMovieList = new ArrayList<>();

    private final MovieAdapterClickHandler mMovieAdapterClickHandler;

    public MovieAdapter(MovieAdapterClickHandler movieAdapterClickHandler) {
        mMovieAdapterClickHandler = movieAdapterClickHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void setData(List<MovieData> movieList) {
        mMovieList.clear();
        mMovieList.addAll(movieList);
        notifyDataSetChanged();
    }

    public interface MovieAdapterClickHandler {
        void onItemClicked(MovieData item);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageView;

        int pos;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            pos = position;
            Picasso.get().load(NetworkUtils.getMoviePosterUrl(mMovieList.get(position)
                    .getPosterPath()))
                    .fit().into(imageView);
        }

        @Override
        public void onClick(View v) {
            mMovieAdapterClickHandler.onItemClicked(mMovieList.get(pos));
        }
    }
}
