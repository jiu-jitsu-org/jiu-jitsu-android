package com.kyu.jiu_jitsu.data.module

import com.kyu.jiu_jitsu.data.api.BootStrapService
import com.kyu.jiu_jitsu.data.api.LoginService
import com.kyu.jiu_jitsu.data.api.UserService
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
    fun bootStrapService(@NetworkModule.BaseNetworkExceptToken baseRetrofit: Retrofit): BootStrapService =
        baseRetrofit.create(BootStrapService::class.java)

    @Provides
    @Singleton
    fun userService(@NetworkModule.BaseNetworkIncludeToken baseRetrofit: Retrofit): UserService =
        baseRetrofit.create(UserService::class.java)

}