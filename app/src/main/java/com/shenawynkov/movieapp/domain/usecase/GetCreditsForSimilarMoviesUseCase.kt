package com.shenawynkov.movieapp.domain.usecase

import com.shenawynkov.movieapp.domain.model.GroupedMovieContributors
import com.shenawynkov.movieapp.domain.model.Role
import com.shenawynkov.movieapp.domain.repo.MovieRepository
import com.shenawynkov.movieapp.utils.data.Result
import javax.inject.Inject

class GetCreditsForSimilarMoviesUseCase
    @Inject
    constructor(
        private val movieRepository: MovieRepository,
    ) {
        suspend operator fun invoke(movieIds: List<Int>): Result<GroupedMovieContributors> {
            if (movieIds.isEmpty()) {
                return Result.Success(GroupedMovieContributors(emptyList(), emptyList()))
            }

            return when (val result = movieRepository.getCreditsForMovies(movieIds)) {
                is Result.Success -> {
                    val allContributors = result.data

                    val topActors =
                        allContributors
                            .filter { it.role == Role.ACTOR }
                            .sortedByDescending { it.popularity }
                            .take(5)

                    val topDirectors =
                        allContributors
                            .filter { it.role == Role.DIRECTOR }
                            .sortedByDescending { it.popularity }
                            .take(5)

                    Result.Success(GroupedMovieContributors(actors = topActors, directors = topDirectors))
                }
                is Result.Error -> {
                    Result.Error(result.exception)
                }
            }
        }
    } 
