package com.shenawynkov.movieapp.domain.usecase

import com.shenawynkov.movieapp.domain.model.Movie
import com.shenawynkov.movieapp.domain.repo.MovieRepository
import com.shenawynkov.movieapp.utils.data.Result
import javax.inject.Inject

class GetSimilarMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<List<Movie>> {
        return movieRepository.getSimilarMovies(movieId)
    }
} 