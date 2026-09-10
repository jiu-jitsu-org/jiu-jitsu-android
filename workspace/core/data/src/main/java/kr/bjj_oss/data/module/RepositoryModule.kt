package kr.bjj_oss.data.module

import android.content.Context
import kr.bjj_oss.data.repository.WebSessionRepository
import kr.bjj_oss.data.repository.impl.WebSessionRepositoryImpl
import com.squareup.moshi.Moshi
import kr.bjj_oss.data.api.BootStrapService
import kr.bjj_oss.data.api.CommunityService
import kr.bjj_oss.data.api.ImageKitService
import kr.bjj_oss.data.api.ImageService
import kr.bjj_oss.data.api.LoginService
import kr.bjj_oss.data.api.UserService
import kr.bjj_oss.data.repository.BootStrapRepository
import kr.bjj_oss.data.repository.CommunityRepository
import kr.bjj_oss.data.repository.ImageRepository
import kr.bjj_oss.data.repository.SessionRepository
import kr.bjj_oss.data.repository.SnsLoginRepository
import kr.bjj_oss.data.repository.UserRepository
import kr.bjj_oss.data.repository.impl.BootStrapInfoRepository
import kr.bjj_oss.data.repository.impl.CommunityRepositoryImpl
import kr.bjj_oss.data.repository.impl.ImageRepositoryImpl
import kr.bjj_oss.data.repository.impl.LoginUserRepositoryImpl
import kr.bjj_oss.data.repository.impl.SessionRepositoryImpl
import kr.bjj_oss.data.repository.impl.UserRepositoryImpl
import kr.bjj_oss.data.session.SessionLocalDataSource
import kr.bjj_oss.data.session.TokenRefreshCoordinator
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
