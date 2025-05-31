package com.shenawynkov.movieapp.di

import com.shenawynkov.movieapp.ui.common.utils.ContextStringResourceProvider
import com.shenawynkov.movieapp.ui.common.utils.StringResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class ResourceProviderModule {
    @Binds
    @Singleton
    abstract fun bindStringResourceProvider(contextStringResourceProvider: ContextStringResourceProvider): StringResourceProvider
} 
