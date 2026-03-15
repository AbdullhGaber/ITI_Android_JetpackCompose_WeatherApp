package com.iti.weatherapp.data.di

import com.iti.weatherapp.data.data_sources.local.LocalDataSource
import com.iti.weatherapp.data.data_sources.local.LocalDataSourceImpl
import com.iti.weatherapp.data.data_sources.remote.RemoteDataSource
import com.iti.weatherapp.data.data_sources.remote.RemoteDataSourceImpl
import com.iti.weatherapp.data.repository.Repository
import com.iti.weatherapp.data.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(
        remoteDataSourceImpl: RemoteDataSourceImpl
    ): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindLocalDataSource(
        localDataSourceImpl: LocalDataSourceImpl
    ): LocalDataSource

    @Binds
    @Singleton
    abstract fun bindRepository(
        repositoryImpl: RepositoryImpl
    ): Repository
}