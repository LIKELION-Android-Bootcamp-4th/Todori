package com.mukmuk.todori.di

import com.mukmuk.todori.data.remote.clova.ClovaOcrService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val CLOVA_OCR_INVOKE_URL = "https://q96gfg29f1.apigw.ntruss.com/custom/v1/45398/ac1686917ee48d0462d3fac180a9439881742b875e44e6f5bae96b390c9b5047/general/" // Added '/' at the end
    private const val CLOVA_OCR_SECRET_KEY = "ZWZNVmdvRGNFWFRHeVBlVkRHYXNOTmlMQ0pGdHBnR3U="
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 네트워크 로그 확인용
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
            .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃
            .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃
            .build()
    }

    @Provides
    @Singleton
    fun provideClovaOcrService(okHttpClient: OkHttpClient): ClovaOcrService {
        return Retrofit.Builder()
            .baseUrl(CLOVA_OCR_INVOKE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClovaOcrService::class.java)
    }

    // Secret Key를 ViewModel 등에서 사용할 수 있도록 Provides
    @Provides
    fun provideClovaOcrSecretKey(): String {
        return CLOVA_OCR_SECRET_KEY
    }
}