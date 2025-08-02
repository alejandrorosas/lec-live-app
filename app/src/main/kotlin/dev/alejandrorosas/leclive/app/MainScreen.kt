package dev.alejandrorosas.leclive.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.alejandrorosas.core.di.BottomNavigationItem
import dev.alejandrorosas.core.di.Navigation
import dev.alejandrorosas.strings.R.string

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navigationSet: Set<@JvmSuppressWildcards Navigation>,
    bottomNavigationItemSet: Set<@JvmSuppressWildcards BottomNavigationItem>,
    initialRoute: String,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val bottomBarState = rememberSaveable { mutableStateOf(true) }
    val topBarState = rememberSaveable { mutableStateOf(true) }
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(visible = topBarState.value) {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = string.app_name))
                    },
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = bottomBarState.value) {
                NavigationBar {
                    bottomNavigationItemSet.sortedBy { it.position }.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, stringResource(item.label)) },
                            label = { Text(stringResource(item.label)) },
                            selected = currentRoute == item.route,
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate(item.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = initialRoute, modifier = Modifier.padding(paddingValues)) {
            navigationSet.forEach { it.prepare(this, navController) }
        }
    }
}
