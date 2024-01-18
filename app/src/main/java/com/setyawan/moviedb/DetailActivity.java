package com.setyawan.moviedb;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.setyawan.moviedb.adapter.ReviewAdapter;
import com.setyawan.moviedb.adapter.TrailerAdapter;
import com.setyawan.moviedb.database.DBHelper;
import com.setyawan.moviedb.model.Result;
import com.setyawan.moviedb.model.Review;
import com.setyawan.moviedb.model.ReviewList;
import com.setyawan.moviedb.model.Trailer;
import com.setyawan.moviedb.model.TrailerList;
import com.setyawan.moviedb.utils.ApiClient;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imgHeader, imgPoster;
    private TextView txtTitle, txtOverview, txtVote, txtDate, txtGenre;
    private RecyclerView trailerView, reviewView;
    private List<Trailer> trailerList;
    private List<Review> reviewList;
    private ProgressBar pb_trailers, pb_reviews;
    private FloatingActionButton fab_yes, fab_no;
    private Result movie;
    private DBHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Binding
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        imgHeader = (ImageView) findViewById(R.id.backdrop);
        imgPoster = (ImageView) findViewById(R.id.poster);
        txtTitle = (TextView) findViewById(R.id.title);
        txtOverview = (TextView) findViewById(R.id.overview);
        txtDate = (TextView) findViewById(R.id.date);
        txtVote = (TextView) findViewById(R.id.vote);
        txtGenre = (TextView) findViewById(R.id.genre);
        trailerView = (RecyclerView) findViewById(R.id.trailer);
        reviewView = (RecyclerView) findViewById(R.id.reviews);
        pb_trailers = (ProgressBar) findViewById(R.id.pb_trailers);
        pb_reviews = (ProgressBar) findViewById(R.id.pb_reviews);

        //setup sqlite
        mDbHelper = new DBHelper(this);

        // Get intent from MainActivity
        Intent i = getIntent();
        movie = new GsonBuilder().create().fromJson(i.getStringExtra("movie"),Result.class);
        collapsingToolbar.setTitle(movie.getTitle());
        Glide.with(this).load(ApiInterface.BASE_BACK_URL + movie.getBackdropPath()).into(imgHeader);
        Glide.with(this).load(ApiInterface.BASE_IMG_URL + movie.getPosterPath()).into(imgPoster);
        txtTitle.setText(movie.getTitle());
        txtVote.setText(Html.fromHtml("<b>Avg vote:</b><br>" + movie.getVoteAverage()));
        txtDate.setText(Html.fromHtml("<b>Released date:</b><br>" + movie.getReleaseDate()));
        txtGenre.setText(Html.fromHtml("<b>Genre:</b><br><i>" + mDbHelper.getGenre(movie.getGenreIds()) + "</i>"));

        txtOverview.setText(movie.getOverview());
        trailerView.setLayoutManager(new GridLayoutManager(this,2));
        reviewView.setLayoutManager(new LinearLayoutManager(this));
        loadJSON();


        // FAB (favorite/unfaforite)
        fab_yes = (FloatingActionButton) findViewById(R.id.fab_yes);
        fab_no = (FloatingActionButton) findViewById(R.id.fab_no);

        cekFavorite();

        fab_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_yes.setVisibility(View.INVISIBLE);
                fab_no.setVisibility(View.VISIBLE);

                mDbHelper.addFavorite(movie.getId(),new GsonBuilder().create().toJson(movie));
                Snackbar.make(view, "Added to favorite lists", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        fab_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab_no.setVisibility(View.INVISIBLE);
                fab_yes.setVisibility(View.VISIBLE);

                mDbHelper.deleteFavorite(movie.getId());
                Snackbar.make(view, "Removed from favorite lists", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadJSON() {
        int id = movie.getId();

        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        // get trailer list
        Call<TrailerList> tCall = apiInterface.getTrailer(id);
        pb_trailers.setVisibility(View.VISIBLE);
        tCall.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(Call<TrailerList> call, Response<TrailerList> response) {
                pb_trailers.setVisibility(View.GONE);
                trailerList = response.body().getResults();
                TrailerAdapter trailerAdapter = new TrailerAdapter(DetailActivity.this,trailerList);
                trailerView.setAdapter(trailerAdapter);
            }
            @Override
            public void onFailure(Call<TrailerList> call, Throwable t) {

            }
        });

        //get review
        Call<ReviewList> rCall = apiInterface.getReview(id);
        pb_reviews.setVisibility(View.VISIBLE);
        rCall.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                pb_reviews.setVisibility(View.GONE);
                reviewList = response.body().getResults();
                ReviewAdapter reviewAdapter = new ReviewAdapter(DetailActivity.this, reviewList);
                reviewView.setAdapter(reviewAdapter);
            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable t) {

            }
        });
    }

    public void cekFavorite(){
        if (!mDbHelper.isFavorite(movie.getId())){
            fab_yes.setVisibility(View.VISIBLE);
            fab_no.setVisibility(View.INVISIBLE);
        } else  {
            fab_yes.setVisibility(View.INVISIBLE);
            fab_no.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}
