package com.shenawynkov.movieapp.domain.usecase

import com.shenawynkov.movieapp.domain.model.Movie
// import com.shenawynkov.movieapp.domain.model.PagedMoviesDomain // Reverted
import com.shenawynkov.movieapp.domain.repo.MovieRepository
import com.shenawynkov.movieapp.utils.data.Result
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int): Result<List<Movie>> { // Return List<Movie>
        if (query.isBlank()) {
            return Result.Success(emptyList()) // Reverted to original blank query handling
        }
        return movieRepository.searchMovies(query, page)
    }
} 