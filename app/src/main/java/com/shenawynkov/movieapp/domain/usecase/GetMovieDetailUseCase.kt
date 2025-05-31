package com.shenawynkov.movieapp.domain.usecase

import com.shenawynkov.movieapp.domain.model.MovieDetail
import com.shenawynkov.movieapp.domain.repo.MovieRepository
import com.shenawynkov.movieapp.utils.data.Result
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<MovieDetail> {
        return movieRepository.getMovieDetails(movieId)
    }
} 