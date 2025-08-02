package dev.alejandrorosas.leclive.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.alejandrorosas.core.di.BottomNavigationItem
import dev.alejandrorosas.core.di.INITIAL_ROUTE
import dev.alejandrorosas.core.di.Navigation
import dev.alejandrorosas.core.ui.AppTheme
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var navigationSet: Set<@JvmSuppressWildcards Navigation>

    @Inject
    lateinit var bottomNavigationItemSet: Set<@JvmSuppressWildcards BottomNavigationItem>

    @Inject
    @Named(INITIAL_ROUTE)
    lateinit var initialRoute: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen(navigationSet, bottomNavigationItemSet, initialRoute)
            }
        }
    }
}
