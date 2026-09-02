package com.kyu.jiu_jitsu.data.module

import com.kyu.jiu_jitsu.data.BuildConfig
import com.kyu.jiu_jitsu.data.api.interceptor.TokenRefreshInterceptor
import com.kyu.jiu_jitsu.data.session.AccessTokenProvider
import com.kyu.jiu_jitsu.data.session.SessionRequestRevision
import com.kyu.jiu_jitsu.data.session.TokenRefreshCoordinator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.nerdythings.okhttp.profiler.OkHttpProfilerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/** Constructs the three network stacks used by the app. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = BuildConfig.BASE_URL

    // Seconds, not milliseconds. The previous 3,000-second values could leave a failed request
    // hanging for almost an hour and made offline recovery appear frozen.
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BaseNetworkExceptToken

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class BaseNetworkIncludeToken

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ImageKitNetwork

    @Provides
    @Singleton
    @BaseNetworkExceptToken
    fun provideBaseExceptTokenOkHttpClient(): OkHttpClient =
        commonClientBuilder()
            .addProfilerInterceptorIfDebug()
            .build()

    @Provides
    @Singleton
    @BaseNetworkIncludeToken
    fun provideBaseOkHttpClient(
        accessTokenProvider: AccessTokenProvider,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
    ): OkHttpClient = commonClientBuilder()
        .addInterceptor { chain ->
            val session = accessTokenProvider.snapshot()
            val requestBuilder = chain.request().newBuilder()
                .tag(SessionRequestRevision::class.java, SessionRequestRevision(session.revision))
            // Anonymous and pre-login calls must not send an empty `Bearer ` credential.
            session.accessToken?.let { token ->
                requestBuilder.header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
            }
            chain.proceed(requestBuilder.build())
        }
        // Authentication is added first so a refreshed request can compare and replace it.
        .addInterceptor(tokenRefreshInterceptor)
        .addProfilerInterceptorIfDebug()
        .build()

    @Provides
    @Singleton
    fun provideTokenRefreshInterceptor(
        coordinator: TokenRefreshCoordinator,
        moshi: Moshi,
    ): TokenRefreshInterceptor = TokenRefreshInterceptor(coordinator, moshi)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    @Singleton
    @BaseNetworkIncludeToken
    fun provideBaseRetrofit(
        @BaseNetworkIncludeToken okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit = createRetrofit(BASE_URL, okHttpClient, moshiConverterFactory)

    @Provides
    @Singleton
    @BaseNetworkExceptToken
    fun provideBaseExceptTokenRetrofit(
        @BaseNetworkExceptToken okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit = createRetrofit(BASE_URL, okHttpClient, moshiConverterFactory)

    @Provides
    @Singleton
    @ImageKitNetwork
    fun provideImageKitRetrofit(
        @BaseNetworkExceptToken okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit = createRetrofit(IMAGE_KIT_UPLOAD_BASE_URL, okHttpClient, moshiConverterFactory)

    /** Applies identical transport defaults so authenticated and anonymous clients cannot drift. */
    private fun commonClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .cache(null)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header(ACCEPT_HEADER, APPLICATION_JSON)
                    .build(),
            )
        }

    /** Keeps request and response profiling out of release builds. */
    private fun OkHttpClient.Builder.addProfilerInterceptorIfDebug(): OkHttpClient.Builder = apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(OkHttpProfilerInterceptor())
        }
    }

    private fun createRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        converterFactory: MoshiConverterFactory,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()

    private const val IMAGE_KIT_UPLOAD_BASE_URL = "https://upload.imagekit.io/"
    private const val ACCEPT_HEADER = "Accept"
    private const val AUTHORIZATION_HEADER = "Authorization"
    private const val APPLICATION_JSON = "application/json"
    private const val BEARER_PREFIX = "Bearer "
}
