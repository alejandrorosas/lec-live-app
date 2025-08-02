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
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

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
    fun provideLeaguepediaService(): LeaguepediaService {
        val contentType = "application/json".toMediaType()
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl("https://lol.fandom.com/")
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()

        return retrofit.create(LeaguepediaService::class.java)
    }

    @Provides
    fun provideLeaguepediaClient(leaguepediaService: LeaguepediaService): LeaguepediaClient = LeaguepediaClientImpl(leaguepediaService)
}
