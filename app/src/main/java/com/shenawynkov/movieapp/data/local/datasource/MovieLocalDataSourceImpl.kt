package com.shenawynkov.movieapp.data.local.datasource

import com.shenawynkov.movieapp.data.local.db.doa.WatchlistDao
import com.shenawynkov.movieapp.data.local.db.entities.WatchlistEntry
import javax.inject.Inject

class MovieLocalDataSourceImpl
    @Inject
    constructor(
        private val dao: WatchlistDao,
    ) : MovieLocalDataSource {
        override suspend fun addToWatchlist(movieId: Int) {
            dao.insert(WatchlistEntry(movieId = movieId))
        }

        override suspend fun removeFromWatchlist(movieId: Int) {
            dao.delete(movieId)
        }

        override suspend fun isOnWatchlist(movieId: Int): Boolean = dao.exists(movieId)
    }
