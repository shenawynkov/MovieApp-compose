package com.shenawynkov.movieapp.data.local.db.doa

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shenawynkov.movieapp.data.local.db.entities.WatchlistEntry

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WatchlistEntry)

    @Query("DELETE FROM watchlist WHERE movieId = :id")
    suspend fun delete(id: Int)

    @Query("SELECT EXISTS(SELECT * FROM watchlist WHERE movieId = :id)")
    suspend fun exists(id: Int): Boolean
}
