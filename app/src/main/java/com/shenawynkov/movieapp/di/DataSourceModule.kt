package com.shenawynkov.movieapp.di

import com.shenawynkov.movieapp.data.local.datasource.MovieLocalDataSource
import com.shenawynkov.movieapp.data.local.datasource.MovieLocalDataSourceImpl
import com.shenawynkov.movieapp.data.remote.datasource.MovieRemoteDataSource
import com.shenawynkov.movieapp.data.remote.datasource.MovieRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindMovieRemoteDataSource(movieRemoteDataSourceImpl: MovieRemoteDataSourceImpl): MovieRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMovieLocalDataSource(movieLocalDataSourceImpl: MovieLocalDataSourceImpl): MovieLocalDataSource
} 
