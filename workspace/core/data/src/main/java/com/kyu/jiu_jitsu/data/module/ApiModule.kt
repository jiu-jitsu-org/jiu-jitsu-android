package com.kyu.jiu_jitsu.data.module

import com.kyu.jiu_jitsu.data.api.LoginApiService
import com.kyu.jiu_jitsu.data.api.RandomUserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun loginApiService(@NetworkModule.BaseNetworkExceptToken baseRetrofit: Retrofit): LoginApiService =
        baseRetrofit.create(LoginApiService::class.java)

    @Provides
    @Singleton
    fun randomUserApiService(@NetworkModule.BaseNetworkExceptToken baseRetrofit: Retrofit): RandomUserService =
        baseRetrofit.create(RandomUserService::class.java)
}