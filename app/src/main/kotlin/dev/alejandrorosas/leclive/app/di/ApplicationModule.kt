package dev.alejandrorosas.leclive.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import dev.alejandrorosas.core.di.BottomNavigationItem
import dev.alejandrorosas.core.di.Navigation

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    @ElementsIntoSet
    fun primeEmptyNavigationSet(): Set<Navigation> = emptySet()

    @Provides
    @ElementsIntoSet
    fun primeEmptyBottomNavigationItemSet(): Set<BottomNavigationItem> = emptySet()
}
