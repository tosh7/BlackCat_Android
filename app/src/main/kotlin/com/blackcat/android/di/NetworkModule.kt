package com.blackcat.android.di

import com.blackcat.android.data.remote.service.CarrierTrackingService
import com.blackcat.android.data.remote.service.JapanPostTrackingService
import com.blackcat.android.data.remote.service.SagawaTrackingService
import com.blackcat.android.data.remote.service.YamatoTrackingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android) BlackCat/1.0")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideYamatoTrackingService(client: OkHttpClient): YamatoTrackingService {
        return YamatoTrackingService(client)
    }

    @Provides
    @Singleton
    fun provideSagawaTrackingService(client: OkHttpClient): SagawaTrackingService {
        return SagawaTrackingService(client)
    }

    @Provides
    @Singleton
    fun provideJapanPostTrackingService(client: OkHttpClient): JapanPostTrackingService {
        return JapanPostTrackingService(client)
    }

    @Provides
    @Singleton
    fun provideCarrierTrackingService(
        yamatoService: YamatoTrackingService,
        sagawaService: SagawaTrackingService,
        japanPostService: JapanPostTrackingService
    ): CarrierTrackingService {
        return CarrierTrackingService(yamatoService, sagawaService, japanPostService)
    }
}
