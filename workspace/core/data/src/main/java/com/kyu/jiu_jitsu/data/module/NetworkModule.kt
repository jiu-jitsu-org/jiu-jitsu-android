package com.kyu.jiu_jitsu.data.module

import com.kyu.jiu_jitsu.data.BuildConfig
import com.kyu.jiu_jitsu.data.api.interceptor.TokenRefreshInterceptor
import com.kyu.jiu_jitsu.data.datastore.SecurePreferences
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val PRINT_LOG = true
    private const val BASE_URL = BuildConfig.BASE_URL // 기본 URL

    private const val CONNECT_TIMEOUT = 3000L // 커넥션 타임
    private const val WRITE_TIMEOUT = 3000L // 쓰기 타임
    private const val READ_TIMEOUT = 3000L // 읽기 타임

    private var userToken = ""

    fun String?.setUserToken() {
        userToken = this ?: ""
    }

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
        if (PRINT_LOG) {
            OkHttpClient.Builder()
//                .cookieJar(JavaNetCookieJar(CookieManager()))       // 쿠키 매니저 연결
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)  // 쓰기 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)      // 읽기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)        // 연결 타임아웃 시간 설정
                .cache(null)                                 // 캐시사용 안함
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .header("Accept", "application/json")
                            .build()
                    )
                }
//                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(OkHttpProfilerInterceptor())
                .build()
        } else {
            OkHttpClient.Builder()
//                .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS)) // https 관련 보안 옵션
//                .cookieJar(JavaNetCookieJar(CookieManager()))       // 쿠키 매니저 연결
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)  // 쓰기 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)      // 읽기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)        // 연결 타임아웃 시간 설정
                .cache(null)                                 // 캐시사용 안함
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .header("Accept", "application/json")
                            .build()
                    )
                }
//                .addInterceptor(getLoggingInterceptor())
                .build()
        }

    @Provides
    @Singleton
    @BaseNetworkIncludeToken
    fun provideBaseOkHttpClient(
        tokenRefreshInterceptor: TokenRefreshInterceptor,
    ): OkHttpClient =
        if (PRINT_LOG) {
            OkHttpClient.Builder()
//                .cookieJar(JavaNetCookieJar(CookieManager()))       // 쿠키 매니저 연결
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)  // 쓰기 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)      // 읽기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)        // 연결 타임아웃 시간 설정
                .cache(null)                                 // 캐시사용 안함
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization","Bearer $userToken")
                            .build()
                    )
                }
                // Authorization 헤더를 붙인 뒤 TokenRefreshInterceptor를 실행해야 한다.
                // 그래야 refresh 완료 후 원 요청을 재시도할 때 기존 요청 토큰과 최신 저장 토큰을 비교할 수 있다.
                .addInterceptor(tokenRefreshInterceptor)
//                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(OkHttpProfilerInterceptor())
                .build()
        } else {
            OkHttpClient.Builder()
//                .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS)) // https 관련 보안 옵션
//                .cookieJar(JavaNetCookieJar(CookieManager()))       // 쿠키 매니저 연결
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)  // 쓰기 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)      // 읽기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)        // 연결 타임아웃 시간 설정
                .cache(null)                                 // 캐시사용 안함
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization","Bearer $userToken")
                            .build()
                    )
                }
                // Authorization 헤더를 붙인 뒤 TokenRefreshInterceptor를 실행해야 한다.
                // 그래야 refresh 완료 후 원 요청을 재시도할 때 기존 요청 토큰과 최신 저장 토큰을 비교할 수 있다.
                .addInterceptor(tokenRefreshInterceptor)
//                .addInterceptor(getLoggingInterceptor())
                .build()
        }

    @Provides
    @Singleton
    fun provideTokenRefreshInterceptor(
        securePreferences: SecurePreferences,
        @BaseNetworkExceptToken refreshClient: OkHttpClient,
        moshi: Moshi,
    ): TokenRefreshInterceptor =
        TokenRefreshInterceptor(
            securePreferences = securePreferences,
            refreshClient = refreshClient,
            moshi = moshi,
            baseUrl = BASE_URL,
        )

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(
        moshi: Moshi,
    ) : MoshiConverterFactory = MoshiConverterFactory.create(moshi)

    @Provides
    @Singleton
    @BaseNetworkIncludeToken
    fun providerBaseRetrofit(
        @BaseNetworkIncludeToken okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)                 // MoshiConverter 적용
            .build()

    @Provides
    @Singleton
    @BaseNetworkExceptToken
    fun providerBaseExceptTokenRetrofit(
        @BaseNetworkExceptToken okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)                 // MoshiConverter 적용
            .build()

    @Provides
    @Singleton
    @ImageKitNetwork
    fun providerImageKitRetrofit(
        @BaseNetworkExceptToken okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(IMAGE_KIT_UPLOAD_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()

    private const val IMAGE_KIT_UPLOAD_BASE_URL = "https://upload.imagekit.io/"
}
