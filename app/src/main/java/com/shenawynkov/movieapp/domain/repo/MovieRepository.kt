package com.shenawynkov.movieapp.domain.repo

import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.model.MovieContributor
import com.shenawynkov.movieapp.domain.model.MovieDetail
import com.shenawynkov.movieapp.utils.data.Result

interface MovieRepository {
    suspend fun getPopularMovies(page: Int): Result<List<Movie>>

    suspend fun searchMovies(
        query: String,
        page: Int,
    ): Result<List<Movie>>

    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail>

    suspend fun getSimilarMovies(movieId: Int): Result<List<Movie>>

    suspend fun getCreditsForMovies(movieIds: List<Int>): Result<List<MovieContributor>>

    suspend fun isOnWatchlist(movieId: Int): Boolean

    suspend fun addToWatchlist(movieId: Int)

    suspend fun removeFromWatchlist(movieId: Int)
}
