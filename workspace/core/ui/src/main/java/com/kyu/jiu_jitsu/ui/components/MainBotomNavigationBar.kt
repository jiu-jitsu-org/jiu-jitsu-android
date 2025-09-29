package com.kyu.jiu_jitsu.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kyu.jiu_jitsu.ui.routes.BlueScreen
import com.kyu.jiu_jitsu.ui.routes.RedScreen
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.CoolGray300
import com.kyu.jiu_jitsu.ui.theme.CoolGray500
import com.kyu.jiu_jitsu.ui.theme.White

@Composable
fun MainBottomNavigationBar(
    navHostController: NavHostController,
    navItems: List<Any>,
) {
    NavigationBar(
        containerColor = White,
        tonalElevation = 0.dp,
    ) {
        val backStack by navHostController.currentBackStackEntryAsState()
        val destination = backStack?.destination

        navItems.forEach { route ->
            val selected =
                destination?.hierarchy?.any { it.hasRoute(route::class) } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navHostController.navigate(route) {
                        // start, destination 까지 올라가 기존 스택 누적 방지
                        popUpTo(navHostController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val iv = when (route) {
                        RedScreen -> { if(selected) Icons.Filled.Android else Icons.Outlined.Android }
                        BlueScreen -> { if(selected) Icons.Filled.Notifications else Icons.Outlined.Notifications }
                        else -> { if(selected) Icons.Filled.Home else Icons.Outlined.Home }
                    }

                    Icon(iv, contentDescription = route::class.simpleName)

                },
                label = { Text(route::class.simpleName ?: "") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Blue500,
                    selectedTextColor = Blue500,
                    indicatorColor = White,
                    unselectedIconColor = CoolGray300,
                    unselectedTextColor = CoolGray500,
                )
            )
        }
    }
}