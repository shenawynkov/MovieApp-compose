package com.shenawynkov.movieapp.data.repository.impl

import com.shenawynkov.movieapp.data.local.datasource.MovieLocalDataSource
import com.shenawynkov.movieapp.data.mappers.toDomain
import com.shenawynkov.movieapp.data.mappers.toMovieContributor
import com.shenawynkov.movieapp.data.remote.datasource.MovieRemoteDataSource
import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.model.MovieContributor
import com.shenawynkov.movieapp.domain.model.MovieDetail
import com.shenawynkov.movieapp.domain.model.Role
import com.shenawynkov.movieapp.domain.repo.MovieRepository
import com.shenawynkov.movieapp.utils.data.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: MovieRemoteDataSource,
        private val localDataSource: MovieLocalDataSource,
    ) : MovieRepository {
        override suspend fun getPopularMovies(page: Int): Result<List<Movie>> =
            when (val response = remoteDataSource.getPopularMovies(page = page)) {
                is Result.Success -> {
                    val domainMovies =
                        response.data.results.map { dto ->
                            dto.toDomain(localDataSource)
                        }
                    Result.Success(domainMovies)
                }

                is Result.Error -> response
            }

        override suspend fun searchMovies(
            query: String,
            page: Int,
        ): Result<List<Movie>> =
            when (val response = remoteDataSource.searchMovies(query = query, page = page)) {
                is Result.Success -> {
                    val domainMovies =
                        response.data.results.map { dto ->
                            dto.toDomain(localDataSource)
                        }
                    Result.Success(domainMovies)
                }

                is Result.Error -> response
            }

        override suspend fun getMovieDetails(movieId: Int): Result<MovieDetail> =
            when (val response = remoteDataSource.getMovieDetails(movieId = movieId)) {
                is Result.Success -> {
                    Result.Success(response.data.toDomain(localDataSource))
                }

                is Result.Error -> response
            }

        override suspend fun getSimilarMovies(movieId: Int): Result<List<Movie>> =
            when (
                val response =
                    remoteDataSource.getSimilarMovies(movieId = movieId, page = 1)
            ) {
                is Result.Success -> {
                    val domainMovies =
                        response.data.results
                            .take(5)
                            .map { dto -> dto.toDomain(localDataSource) }
                    Result.Success(domainMovies)
                }

                is Result.Error -> response
            }

        override suspend fun getCreditsForMovies(movieIds: List<Int>): Result<List<MovieContributor>> =
            coroutineScope {
                if (movieIds.isEmpty()) {
                    return@coroutineScope Result.Success(emptyList<MovieContributor>())
                }
                val allContributors = mutableListOf<MovieContributor>()
                val deferredCredits =
                    movieIds.map { id ->
                        async { remoteDataSource.getMovieCredits(movieId = id) }
                    }

                var firstError: Result.Error? = null
                var successfulFetches = 0

                deferredCredits.forEach { deferred ->
                    when (val creditResult = deferred.await()) {
                        is Result.Success -> {
                            successfulFetches++
                            creditResult.data.cast.forEach {
                                if (allContributors.none { c -> c.id == it.id && c.role == Role.ACTOR }) {
                                    allContributors.add(it.toMovieContributor())
                                }
                            }
                            creditResult.data.crew.forEach {
                                if (it.job == "Director" && allContributors.none { c -> c.id == it.id && c.role == Role.DIRECTOR }) {
                                    allContributors.add(it.toMovieContributor())
                                }
                            }
                        }

                        is Result.Error -> {
                            if (firstError == null) {
                                firstError = creditResult
                            }
                        }
                    }
                }

                if (firstError != null && successfulFetches == 0) {
                    return@coroutineScope firstError!!
                }
                Result.Success(allContributors.distinctBy { it.id to it.role })
            }

        override suspend fun isOnWatchlist(movieId: Int): Boolean = localDataSource.isOnWatchlist(movieId = movieId)

        override suspend fun addToWatchlist(movieId: Int) {
            localDataSource.addToWatchlist(movieId)
        }

        override suspend fun removeFromWatchlist(movieId: Int) {
            localDataSource.removeFromWatchlist(movieId = movieId)
        }
    } 
