package com.shenawynkov.movieapp.di

import android.content.Context
import androidx.room.Room
import com.shenawynkov.movieapp.data.local.db.MovieDatabase
import com.shenawynkov.movieapp.data.local.db.doa.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): MovieDatabase =
        Room
            .databaseBuilder(
                context,
                MovieDatabase::class.java,
                "movie_database",
            ).build()

    @Provides
    fun provideWatchlistDao(db: MovieDatabase): WatchlistDao = db.watchlistDao()
}
