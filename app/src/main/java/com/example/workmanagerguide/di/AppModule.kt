package com.example.workmanagerguide.di

import com.example.workmanagerguide.FileApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFileApi(): FileApi {
        return Retrofit.Builder()
            .baseUrl("https://pl-coding.com")
            .build()
            .create(FileApi::class.java)
    }

}