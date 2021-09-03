package com.example.moviedbjava.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviedbjava.models.MovieModel;
import com.example.moviedbjava.repositories.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    // this class is used for ViewModel

    private MovieRepository movieRepository;

    // constructor
    public MovieListViewModel() {
        movieRepository = MovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getMovies() {
        return movieRepository.getMovies();
    }
    public LiveData<List<MovieModel>> getPop() {
        return movieRepository.getPop();
    }

    // calling method in view-model
    public void searchMovieApi(String query, int pageNumber) {
        movieRepository.serchMovieApi(query, pageNumber);
    }

    public void searchMoviePop(int pageNumber) {
        movieRepository.serchMoviePop(pageNumber);
    }

    public void searchNextpage() {
        movieRepository.searchNextPage();
    }
}
