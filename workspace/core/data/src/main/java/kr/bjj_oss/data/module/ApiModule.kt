package kr.bjj_oss.data.module

import kr.bjj_oss.data.api.BootStrapService
import kr.bjj_oss.data.api.CommunityService
import kr.bjj_oss.data.api.ImageKitService
import kr.bjj_oss.data.api.ImageService
import kr.bjj_oss.data.api.LoginService
import kr.bjj_oss.data.api.UserService
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

    @Provides
    @Singleton
    fun communityService(@NetworkModule.BaseNetworkIncludeToken baseRetrofit: Retrofit): CommunityService =
        baseRetrofit.create(CommunityService::class.java)

    @Provides
    @Singleton
    fun imageService(@NetworkModule.BaseNetworkIncludeToken baseRetrofit: Retrofit): ImageService =
        baseRetrofit.create(ImageService::class.java)

    @Provides
    @Singleton
    fun imageKitService(@NetworkModule.ImageKitNetwork imageKitRetrofit: Retrofit): ImageKitService =
        imageKitRetrofit.create(ImageKitService::class.java)

}
