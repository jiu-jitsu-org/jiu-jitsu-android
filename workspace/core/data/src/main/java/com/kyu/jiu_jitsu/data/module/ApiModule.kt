package com.kyu.jiu_jitsu.data.module

import com.kyu.jiu_jitsu.data.api.LoginService
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
    fun loginService(@NetworkModule.BaseNetworkExceptToken baseRetrofit: Retrofit): LoginService =
        baseRetrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun randomUserApiService(@NetworkModule.BaseNetworkExceptToken baseRetrofit: Retrofit): RandomUserService =
        baseRetrofit.create(RandomUserService::class.java)
}