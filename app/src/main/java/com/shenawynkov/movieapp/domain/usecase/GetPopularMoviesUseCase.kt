package com.shenawynkov.movieapp.domain.usecase

import com.shenawynkov.movieapp.domain.model.Movie
// import com.shenawynkov.movieapp.domain.model.PagedMoviesDomain // Reverted
import com.shenawynkov.movieapp.domain.repo.MovieRepository
import com.shenawynkov.movieapp.utils.data.Result
import javax.inject.Inject

class GetPopularMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(page: Int): Result<List<Movie>> {
        return movieRepository.getPopularMovies(page)
    }
} 