package com.setyawan.moviedb.utils;

import com.setyawan.moviedb.model.GenreList;
import com.setyawan.moviedb.model.Movie;
import com.setyawan.moviedb.model.MovieDetails;
import com.setyawan.moviedb.model.ReviewList;
import com.setyawan.moviedb.model.TrailerList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Pad on 8/5/2017.
 */

public interface ApiInterface {
    final String API_KEY = "143165c64007c45baa62b14ba4219b7d";
    public static String BASE_IMG_URL = "https://image.tmdb.org/t/p/w185";
    public static String BASE_BACK_URL = "https://image.tmdb.org/t/p/w500";

    @GET("popular?api_key=" + API_KEY)
    Call<Movie> getPopular();

    @GET("top_rated?api_key=" + API_KEY)
    Call<Movie> getTopRated();

    @GET("upcoming?api_key=" + API_KEY)
    Call<Movie> getUpcoming();

    @GET("now_playing?api_key=" + API_KEY)
    Call<Movie> getNowPlaying();

    @GET("{id}/videos?api_key=" + API_KEY)
    Call<TrailerList> getTrailer(@Path("id") int id);

    @GET("{id}/reviews?api_key=" + API_KEY)
    Call<ReviewList> getReview(@Path("id") int id);

    @GET("/{id}?api_key=" + API_KEY)
    Call<MovieDetails> getMovieDetails(@Path("id") int id);

    @GET("{type}/list?api_key=" + API_KEY)
    Call<GenreList> getGenre(@Path("type") String type);
}
