package com.kyu.jiu_jitsu.data.module

import android.content.Context
import com.kyu.jiu_jitsu.data.repository.WebSessionRepository
import com.kyu.jiu_jitsu.data.repository.impl.WebSessionRepositoryImpl
import com.squareup.moshi.Moshi
import com.kyu.jiu_jitsu.data.api.BootStrapService
import com.kyu.jiu_jitsu.data.api.CommunityService
import com.kyu.jiu_jitsu.data.api.ImageKitService
import com.kyu.jiu_jitsu.data.api.ImageService
import com.kyu.jiu_jitsu.data.api.LoginService
import com.kyu.jiu_jitsu.data.api.UserService
import com.kyu.jiu_jitsu.data.repository.BootStrapRepository
import com.kyu.jiu_jitsu.data.repository.CommunityRepository
import com.kyu.jiu_jitsu.data.repository.ImageRepository
import com.kyu.jiu_jitsu.data.repository.SessionRepository
import com.kyu.jiu_jitsu.data.repository.SnsLoginRepository
import com.kyu.jiu_jitsu.data.repository.UserRepository
import com.kyu.jiu_jitsu.data.repository.impl.BootStrapInfoRepository
import com.kyu.jiu_jitsu.data.repository.impl.CommunityRepositoryImpl
import com.kyu.jiu_jitsu.data.repository.impl.ImageRepositoryImpl
import com.kyu.jiu_jitsu.data.repository.impl.LoginUserRepositoryImpl
import com.kyu.jiu_jitsu.data.repository.impl.SessionRepositoryImpl
import com.kyu.jiu_jitsu.data.repository.impl.UserRepositoryImpl
import com.kyu.jiu_jitsu.data.session.SessionLocalDataSource
import com.kyu.jiu_jitsu.data.session.TokenRefreshCoordinator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWebSessionRepository(store: SessionLocalDataSource, refresh: TokenRefreshCoordinator, moshi: Moshi): WebSessionRepository =
        WebSessionRepositoryImpl(store, refresh, moshi)


    @Provides
    @Singleton
    fun provideSnsLoginRepository(
        loginService: LoginService
    ): SnsLoginRepository = LoginUserRepositoryImpl(loginService)

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

    @Provides
    @Singleton
    fun provideCommunityRepository(
        communityService: CommunityService
    ): CommunityRepository = CommunityRepositoryImpl(communityService)

    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context,
        imageService: ImageService,
        imageKitService: ImageKitService,
        userService: UserService,
    ): ImageRepository = ImageRepositoryImpl(context, imageService, imageKitService, userService)

    @Provides
    @Singleton
    fun provideSessionRepository(
        localDataSource: SessionLocalDataSource,
        refreshCoordinator: TokenRefreshCoordinator,
        communityRepository: CommunityRepository,
    ): SessionRepository = SessionRepositoryImpl(localDataSource, refreshCoordinator, communityRepository)

}
