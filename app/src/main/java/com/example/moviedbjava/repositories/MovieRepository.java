package com.example.moviedbjava.repositories;

import androidx.lifecycle.LiveData;

import com.example.moviedbjava.models.MovieModel;
import com.example.moviedbjava.request.MovieApiClient;

import java.util.List;

public class MovieRepository {

    // This class is acting as repositories
    private static MovieRepository instance;

    private MovieApiClient movieApiClient;

    private String mQuery;
    private int mPageNumber;

    public static MovieRepository getInstance() {
        if (instance == null) {
            instance = new MovieRepository();
        }
        return instance;
    }

    private MovieRepository() {
        movieApiClient = MovieApiClient.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies() {
        return movieApiClient.getMovies();
    }
    public LiveData<List<MovieModel>> getPop() {
        return movieApiClient.getMoviesPop();
    }

    // calling the method in repository
    public void serchMovieApi(String query, int pageNumber) {
        mQuery = query;
        mPageNumber = pageNumber;
    }

    public void serchMoviePop(int pageNumber) {
        mPageNumber = pageNumber;
        movieApiClient.searchMoviesPop(pageNumber);
    }

    public void searchNextPage() {
        serchMovieApi(mQuery, mPageNumber + 1);
    }
}