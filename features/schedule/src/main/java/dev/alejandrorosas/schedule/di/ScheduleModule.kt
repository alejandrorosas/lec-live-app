package dev.alejandrorosas.schedule.di

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.alejandrorosas.core.di.BottomNavigationItem
import dev.alejandrorosas.core.di.Navigation
import dev.alejandrorosas.schedule.ui.Schedule
import dev.alejandrorosas.strings.R.string

@Module
@InstallIn(SingletonComponent::class)
class ScheduleModule {
    @Provides
    @IntoSet
    fun provideScheduleNavigation() =
        Navigation { navController ->
            composable("schedule") {
                Schedule(navController)
            }
        }

    @Provides
    @IntoSet
    fun provideStandingsBottomNavigationItem() =
        object : BottomNavigationItem {
            override val position = 2
            override val route = "schedule"
            override val label = string.screenname_schedule
            override val icon = Icons.Default.DateRange
        }
}
