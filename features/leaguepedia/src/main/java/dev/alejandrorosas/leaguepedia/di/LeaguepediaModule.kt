package dev.alejandrorosas.leaguepedia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.alejandrorosas.leaguepedia.LeaguepediaClientImpl
import dev.alejandrorosas.leaguepedia.api.LeaguepediaService
import dev.alejandrorosas.leaguepedia.api.LeaguepediaServiceFactory
import dev.alejandrorosas.leaguepedia.contract.LeaguepediaClient

@Module
@InstallIn(SingletonComponent::class)
class LeaguepediaModule {
    @Provides
    fun provideLeaguepediaService(): LeaguepediaService = LeaguepediaServiceFactory.create("https://lol.fandom.com/")

    @Provides
    fun provideLeaguepediaClient(leaguepediaService: LeaguepediaService): LeaguepediaClient = LeaguepediaClientImpl(leaguepediaService)
}
