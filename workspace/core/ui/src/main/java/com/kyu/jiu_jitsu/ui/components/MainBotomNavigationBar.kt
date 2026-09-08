package com.kyu.jiu_jitsu.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kyu.jiu_jitsu.ui.R
import com.kyu.jiu_jitsu.ui.routes.SettingScreen
import com.kyu.jiu_jitsu.ui.routes.HomeScreen
import com.kyu.jiu_jitsu.ui.theme.Blue500
import com.kyu.jiu_jitsu.ui.theme.CoolGray300
import com.kyu.jiu_jitsu.ui.theme.CoolGray500
import com.kyu.jiu_jitsu.ui.theme.White

@Composable
fun <T : Any> MainBottomNavigationBar(
    navHostController: NavHostController,
    navItems: List<T>,
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
                    @DrawableRes val iv = when (route) {
                        HomeScreen -> R.drawable.ic_square_dashed
                        SettingScreen -> R.drawable.ic_square_dashed
                        else -> R.drawable.ic_square_dashed
                    }

                    Icon(
                        painter = painterResource(iv),
                        contentDescription = "",
                        tint = if(selected) Blue500 else CoolGray300
                    )

                },
                label = { Text(if (route == HomeScreen) stringResource(R.string.nav_community) else route::class.simpleName ?: "") },
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
