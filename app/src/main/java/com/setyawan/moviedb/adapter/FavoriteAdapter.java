package com.setyawan.moviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.setyawan.moviedb.DetailActivity;
import com.setyawan.moviedb.R;
import com.setyawan.moviedb.database.DBHelper;
import com.setyawan.moviedb.model.Result;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.List;

/**
 * Created by Pad on 8/8/2017.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {
    private List<Result> resultList;
    private Context context;

    public FavoriteAdapter(Context context) {
        this.context = context;
        DBHelper mDbHelper = new DBHelper(context);
        resultList = mDbHelper.getAllFavorite();
    }

    @Override
    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_movie,parent,false);
        return new FavoriteHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteHolder holder, int position) {
        final Result result = resultList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(ApiInterface.BASE_IMG_URL + result.getPosterPath())
                .into(holder.moviePoster);
        holder.title.setText(result.getTitle());
        holder.rate.setText(String.valueOf(result.getVoteAverage()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("movie", new GsonBuilder().create().toJson(result));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    class FavoriteHolder extends RecyclerView.ViewHolder {
        ImageView moviePoster;
        TextView title, rate;
        public FavoriteHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
            title = (TextView) itemView.findViewById(R.id.title);
            rate = (TextView) itemView.findViewById(R.id.rate);
        }
    }
}
