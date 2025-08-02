package dev.alejandrorosas.results.di

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dev.alejandrorosas.core.di.BottomNavigationItem
import dev.alejandrorosas.core.di.INITIAL_ROUTE
import dev.alejandrorosas.core.di.Navigation
import dev.alejandrorosas.results.ui.Results
import dev.alejandrorosas.strings.R.string
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class ResultsModule {
    @Provides
    @Named(INITIAL_ROUTE)
    fun provideInitialRoute(): String = "results"

    @Provides
    @IntoSet
    fun provideResultsNavigation() =
        Navigation { navController ->
            composable("results") {
                Results(navController)
            }
        }

    @Provides
    @IntoSet
    fun provideHomeBottomNavigationItem() =
        object : BottomNavigationItem {
            override val position = 1
            override val route = "results"
            override val label = string.screenname_results
            override val icon = Icons.Default.Home
        }
}
