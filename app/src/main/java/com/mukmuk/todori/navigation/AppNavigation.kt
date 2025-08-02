package com.mukmuk.todori.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mukmuk.todori.ui.screen.community.CommunityScreen
import com.mukmuk.todori.ui.screen.home.HomeScreen
import com.mukmuk.todori.ui.screen.home.HomeViewModel
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingScreen
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingViewModel
import com.mukmuk.todori.ui.screen.mypage.MyPageScreen
import com.mukmuk.todori.ui.screen.stats.StatsScreen
import com.mukmuk.todori.ui.screen.todo.TodoScreen

@Composable
fun AppNavigation(navController: NavHostController,modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Todo.route,
        modifier = modifier // 추가!
    ) {
        composable(BottomNavItem.Todo.route) { TodoScreen() }
        composable(BottomNavItem.Stats.route) { StatsScreen() }
        composable(BottomNavItem.Home.route) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(viewModel = homeViewModel, navController = navController)
        }
        composable("home_setting") {
            val homeSettingViewModel: HomeSettingViewModel = viewModel()
            HomeSettingScreen(viewModel = homeSettingViewModel, navController = navController)
        }
        composable(BottomNavItem.Study.route) { CommunityScreen() }
        composable(BottomNavItem.MyPage.route) { MyPageScreen() }
    }
}