package com.shenawynkov.movieapp.data.local.datasource

import com.shenawynkov.movieapp.data.local.db.doa.WatchlistDao
import com.shenawynkov.movieapp.data.local.db.entities.WatchlistEntry
import javax.inject.Inject

class LocalDataSourceImpl
    @Inject
    constructor(
        private val dao: WatchlistDao,
    ) : LocalDataSource {
        override suspend fun addToWatchlist(id: Int) = dao.insert(WatchlistEntry(id))

        override suspend fun removeFromWatchlist(id: Int) = dao.delete(id)

        override suspend fun isOnWatchlist(id: Int) = dao.exists(id)

        override suspend fun getWatchlistIds(): List<Int> = dao.getAllIds()
    }
