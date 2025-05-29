package com.shenawynkov.movieapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shenawynkov.movieapp.data.local.db.doa.WatchlistDao
import com.shenawynkov.movieapp.data.local.db.entities.WatchlistEntry

@Database(
    entities = [WatchlistEntry::class],
    version = 1,
    exportSchema = false,
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
}
