package com.kyu.jiu_jitsu.data.module

import com.kyu.jiu_jitsu.data.api.BootStrapService
import com.kyu.jiu_jitsu.data.api.LoginService
import com.kyu.jiu_jitsu.data.api.UserService
import com.kyu.jiu_jitsu.data.repository.BootStrapRepository
import com.kyu.jiu_jitsu.data.repository.RefreshTokenRepository
import com.kyu.jiu_jitsu.data.repository.SnsLoginRepository
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.data.repository.impl.BootStrapInfoRepository
import com.kyu.jiu_jitsu.data.repository.impl.LoginUserRepositoryImpl
import com.kyu.jiu_jitsu.data.repository.impl.UserRepositoryImpl
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
    fun provideSnsLoginRepository(
        loginService: LoginService
    ): SnsLoginRepository = LoginUserRepositoryImpl(loginService)

    @Provides
    @Singleton
    fun provideRefreshTokenRepository(
        loginService: LoginService
    ): RefreshTokenRepository = LoginUserRepositoryImpl(loginService)

    @Provides
    @Singleton
    fun provideBootStrapRepository(
        bootStrapService: BootStrapService
    ): BootStrapRepository = BootStrapInfoRepository(bootStrapService)

    @Provides
    @Singleton
    fun provideUserRepository(
        userService: UserService
    ): UserRepository = UserRepositoryImpl(userService)

}