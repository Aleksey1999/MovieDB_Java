package com.example.moviedbjava.request;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviedbjava.AppExecutors;
import com.example.moviedbjava.models.MovieModel;
import com.example.moviedbjava.response.MovieSearchResponse;
import com.example.moviedbjava.utils.Credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class MovieApiClient {

    // liveData for search
    private MutableLiveData<List<MovieModel>> mMovies;

    private static MovieApiClient instance;

    // Live Data for popular movies
    private MutableLiveData<List<MovieModel>> mMoviesPop;

    // making Popular runnable
    private RetrieveMoviesRunnablePop retrieveMoviesRunnablePop;

    public static  MovieApiClient getInstance() {
        if (instance == null) {
            instance = new MovieApiClient();
        } return instance;
    }

    private MovieApiClient() {
        mMovies = new MutableLiveData<>();
        mMoviesPop = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getMovies() {
        return  mMovies;
    }
    public LiveData<List<MovieModel>> getMoviesPop() {
        return  mMoviesPop;
    }

    public void searchMoviesPop(int pageNumber) {
        if (retrieveMoviesRunnablePop != null) {
            retrieveMoviesRunnablePop = null;
        }

        retrieveMoviesRunnablePop = new RetrieveMoviesRunnablePop(pageNumber);

        final Future myHandler2 = AppExecutors.getInstance().networkIO().submit(retrieveMoviesRunnablePop);

        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // cancelling the retrofit call
                myHandler2.cancel(true);
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    private class RetrieveMoviesRunnablePop implements Runnable {

        private int pageNumber;
        boolean cancelRequest;

        public RetrieveMoviesRunnablePop(int pageNumber) {
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            // Getting the response objects
            try {
                Response response2 = getPop(pageNumber).execute();
                if (cancelRequest) {
                    return;
                }
                if (response2.code() == 200) {
                    List<MovieModel> list = new ArrayList<>(((MovieSearchResponse)response2.body()).getMovies());
                    if (pageNumber == 1) {
                        // sending data to live data
                        // PostValue: used for background thread
                        // SetValue: not for background thread
                        mMoviesPop.postValue(list);
                    } else {
                        List<MovieModel> currentMovies = mMoviesPop.getValue();
                        currentMovies.addAll(list);
                        mMoviesPop.postValue(currentMovies);
                    }
                } else {
                    String error = response2.errorBody().string();
                    mMoviesPop.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mMoviesPop.postValue(null);
            }
        }

        //Search Method / query
        private Call<MovieSearchResponse> getPop(int pageNumber) {
            return Services.getMovieApi().getPopular(
                    Credentials.API_KEY,
                    pageNumber
            );
        }
        private void cancelRequest() {
            cancelRequest = true;
        }
    }
}