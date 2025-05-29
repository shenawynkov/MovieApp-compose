package com.shenawynkov.movieapp.data.local.datasource

interface LocalDataSource {
    suspend fun addToWatchlist(id: Int)

    suspend fun removeFromWatchlist(id: Int)

    suspend fun isOnWatchlist(id: Int): Boolean

    suspend fun getWatchlistIds(): List<Int>
}
