package com.setyawan.moviedb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.setyawan.moviedb.adapter.FavoriteAdapter;
import com.setyawan.moviedb.adapter.MovieAdapter;
import com.setyawan.moviedb.database.DBHelper;
import com.setyawan.moviedb.model.Genre;
import com.setyawan.moviedb.model.GenreList;
import com.setyawan.moviedb.model.Movie;
import com.setyawan.moviedb.model.Result;
import com.setyawan.moviedb.utils.ApiClient;
import com.setyawan.moviedb.utils.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MovieAdapter adapter;
    ApiInterface apiInterface;
    List<Result> resultList = new ArrayList<>();
    ProgressBar progress;
    LinearLayout load;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MovieAdapter(resultList,MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        load = (LinearLayout) findViewById(R.id.load);
        dbHelper = new DBHelper(MainActivity.this);
        new RequestMovie().execute("popular");
        initGenre();
    }

    private class RequestMovie extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
            load.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... string) {
            String kategori = string[0];
            apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
            Call<Movie> call = null;
            if(kategori.equals("popular")) {
                call = apiInterface.getPopular();
            } else if(kategori.equals("top_rated")) {
                call = apiInterface.getTopRated();
            } else if(kategori.equals("upcoming")) {
                call = apiInterface.getUpcoming();
            } else if(kategori.equals("now_playing")) {
                call = apiInterface.getNowPlaying();
            }

            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    load.setVisibility(View.INVISIBLE);
                    Movie movie = response.body();
                    recyclerView.setAdapter(new MovieAdapter(movie.getResults(), MainActivity.this));
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {

                }
            });
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.popular){
            new RequestMovie().execute("popular");
        } else if(id==R.id.top_rated) {
            new RequestMovie().execute("top_rated");
        } else if(id==R.id.upcoming) {
            new RequestMovie().execute("upcoming");
        } else if(id==R.id.now_playing) {
            new RequestMovie().execute("now_playing");
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if(id==R.id.popular){
                new RequestMovie().execute("popular");
                return true;
            } else if(id==R.id.top_rated) {
                new RequestMovie().execute("top_rated");
                return true;
            } else if(id==R.id.upcoming) {
                new RequestMovie().execute("upcoming");
                return true;
            } else if(id==R.id.now_playing) {
                new RequestMovie().execute("now_playing");
                return true;
            } else if(id==R.id.favorite) {
                recyclerView.setAdapter(new FavoriteAdapter(MainActivity.this));
                return true;
            }
            return false;
        }

    };

    private void initGenre(){
        ApiInterface apiInterface = ApiClient.getRetrofit2().create(ApiInterface.class);
        Call<GenreList> call = apiInterface.getGenre("movie");

        call.enqueue(new Callback<GenreList>() {
            @Override
            public void onResponse(Call<GenreList> call, Response<GenreList> response) {
                    List<Genre> list = response.body().getGenres();
                    for (Genre g:list) {
                        if(!dbHelper.isGenre(g.getId())){
                            dbHelper.addGenre(g.getId(),g.getName());
                        }
                    }

                dbHelper.close();
            }

            @Override
            public void onFailure(Call<GenreList> call, Throwable t) {

            }
        });
    }
}
