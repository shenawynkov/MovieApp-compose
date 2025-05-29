package com.shenawynkov.movieapp.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntry(
    @PrimaryKey val movieId: Int,
)
