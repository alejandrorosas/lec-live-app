package dev.alejandrorosas.leaguepedia.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alejandrorosas.leaguepedia.LeaguepediaClientImpl
import dev.alejandrorosas.leaguepedia.api.LeaguepediaService
import dev.alejandrorosas.leaguepedia.contract.LeaguepediaClient
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.JavaNetCookieJar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.CookieHandler
import java.net.CookieManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LeaguepediaModule {
    @OptIn(ExperimentalSerializationApi::class)
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            coerceInputValues = true
        }

    @Provides
    @Singleton
    fun provideLeaguepediaService(
        okHttpClient: OkHttpClient,
    ): LeaguepediaService {
        val contentType = "application/json".toMediaType()
        val retrofit =
            Retrofit
                .Builder()
                .client(okHttpClient)
                .baseUrl("https://lol.fandom.com/")
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()

        return retrofit.create(LeaguepediaService::class.java)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val cookieHandler: CookieHandler = CookieManager()
        return OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieHandler))
            .build()
    }

    @Provides
    fun provideLeaguepediaClient(
        leaguepediaService: LeaguepediaService,
    ): LeaguepediaClient = LeaguepediaClientImpl(leaguepediaService)
}
