package com.shenawynkov.movieapp.domain.repo

import android.graphics.Movie
import com.shenawynkov.movieapp.utils.data.Result

interface MovieRepository {
    suspend fun getPopularMovies(): Result<List<Movie>>

    suspend fun searchMovies(query: String): Result<List<Movie>>

    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails>

    suspend fun getSimilarMovies(movieId: Int): Result<List<Movie>>

    suspend fun isOnWatchlist(movieId: Int): Boolean

    suspend fun addToWatchlist(movieId: Int)

    suspend fun removeFromWatchlist(movieId: Int)

    suspend fun getWatchlist(): Result<List<Movie>>
}
