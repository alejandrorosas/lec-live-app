package dev.alejandrorosas.core.di

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

const val INITIAL_ROUTE = "home"

@Module
@InstallIn(SingletonComponent::class)
class CoreModule

data class BottomNavigationItem(
    val position: Int,
    val route: String,
    @get:StringRes
    val label: Int,
    val icon: ImageVector,
)
