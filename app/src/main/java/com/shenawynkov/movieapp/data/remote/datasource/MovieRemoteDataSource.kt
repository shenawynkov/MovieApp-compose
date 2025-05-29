package com.shenawynkov.movieapp.data.remote.datasource

import com.shenawynkov.movieapp.data.remote.dto.MovieCreditsDto
import com.shenawynkov.movieapp.data.remote.dto.MovieDetailDto
import com.shenawynkov.movieapp.data.remote.dto.MoviesListResponse
import com.shenawynkov.movieapp.utils.data.Result

interface MovieRemoteDataSource {
    suspend fun getPopularMovies(page: Int): Result<MoviesListResponse>

    suspend fun searchMovies(
        query: String,
        page: Int,
    ): Result<MoviesListResponse>

    suspend fun getMovieDetails(movieId: Int): Result<MovieDetailDto>

    suspend fun getSimilarMovies(
        movieId: Int,
        page: Int,
    ): Result<MoviesListResponse>

    suspend fun getMovieCredits(movieId: Int): Result<MovieCreditsDto>
}
