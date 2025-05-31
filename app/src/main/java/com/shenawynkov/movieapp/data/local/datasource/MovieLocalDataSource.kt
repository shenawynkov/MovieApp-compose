package com.shenawynkov.movieapp.data.local.datasource

interface MovieLocalDataSource {
    suspend fun addToWatchlist(movieId: Int)

    suspend fun removeFromWatchlist(movieId: Int)

    suspend fun isOnWatchlist(movieId: Int): Boolean
}
