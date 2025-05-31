package com.shenawynkov.movieapp.di

import com.shenawynkov.movieapp.data.repository.impl.MovieRepositoryImpl
import com.shenawynkov.movieapp.domain.repo.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository
} 
