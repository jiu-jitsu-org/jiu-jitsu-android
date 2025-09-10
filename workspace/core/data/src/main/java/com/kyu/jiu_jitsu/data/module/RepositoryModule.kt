package com.kyu.jiu_jitsu.data.module

import com.kyu.jiu_jitsu.data.api.RandomUserService
import com.kyu.jiu_jitsu.data.repository.RandomUserRepository
import com.kyu.jiu_jitsu.data.repository.impl.RandomUserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRandomUserRepository(
        randomUserService: RandomUserService
    ): RandomUserRepository = RandomUserRepositoryImpl(randomUserService)

}