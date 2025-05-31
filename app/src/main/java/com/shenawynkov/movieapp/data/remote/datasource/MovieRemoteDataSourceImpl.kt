package com.shenawynkov.movieapp.data.remote.datasource

import com.shenawynkov.movieapp.data.remote.api.MovieApiService
import com.shenawynkov.movieapp.data.remote.dto.MovieCreditsDto
import com.shenawynkov.movieapp.data.remote.dto.MovieDetailDto
import com.shenawynkov.movieapp.data.remote.dto.MoviesListResponse
import com.shenawynkov.movieapp.di.IoDispatcher
import com.shenawynkov.movieapp.utils.data.MovieError
import com.shenawynkov.movieapp.utils.data.Result
import com.shenawynkov.movieapp.utils.network.safeApiCall
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRemoteDataSourceImpl
    @Inject
    constructor(
        private val apiService: MovieApiService,
        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    ) : MovieRemoteDataSource {
        override suspend fun getPopularMovies(page: Int): Result<MoviesListResponse> =
            safeApiCall(ioDispatcher) {
                apiService.getPopularMovies(page = page)
            }

        override suspend fun searchMovies(
            query: String,
            page: Int,
        ): Result<MoviesListResponse> =
            if (query.isBlank()) {
                Result.Error(MovieError.ApiValidationProblem("Query cannot be blank"))
            } else {
                safeApiCall(ioDispatcher) {
                    apiService.searchMovies(query = query.trim(), page = page)
                }
            }

        override suspend fun getMovieDetails(movieId: Int): Result<MovieDetailDto> =
            if (movieId <= 0) {
                Result.Error(MovieError.ApiValidationProblem("Invalid movieId: $movieId"))
            } else {
                safeApiCall(ioDispatcher) {
                    apiService.getMovieDetails(movieId = movieId)
                }
            }

        override suspend fun getSimilarMovies(
            movieId: Int,
            page: Int,
        ): Result<MoviesListResponse> =
            if (movieId <= 0) {
                Result.Error(MovieError.ApiValidationProblem("Invalid movieId: $movieId"))
            } else {
                safeApiCall(ioDispatcher) {
                    apiService.getSimilarMovies(movieId = movieId, page = page)
                }
            }

        override suspend fun getMovieCredits(movieId: Int): Result<MovieCreditsDto> =
            if (movieId <= 0) {
                Result.Error(MovieError.ApiValidationProblem("Invalid movieId: $movieId"))
            } else {
                safeApiCall(ioDispatcher) {
                    apiService.getMovieCredits(movieId = movieId)
                }
            }
    }
