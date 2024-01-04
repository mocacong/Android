package com.konkuk.mocacong.util

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicatonModule {

    @Singleton
    @Provides
    fun appContext(application: MocacongApplication): Context = application.applicationContext
}