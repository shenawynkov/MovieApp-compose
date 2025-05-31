package com.shenawynkov.movieapp.domain.usecase

import com.shenawynkov.movieapp.domain.repo.MovieRepository
import javax.inject.Inject

class ToggleWatchlistUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    /**
     * Toggles the watchlist status of a movie by its ID.
     * @param movieId The ID of the movie to toggle.
     * @return The new watchlist status (true if added/kept, false if removed).
     */
    suspend operator fun invoke(movieId: Int): Boolean {
        val isOnWatchlist = movieRepository.isOnWatchlist(movieId)
        return if (isOnWatchlist) {
            movieRepository.removeFromWatchlist(movieId)
            false // Status after removal
        } else {
            movieRepository.addToWatchlist(movieId)
            true  // Status after addition
        }
    }
} 