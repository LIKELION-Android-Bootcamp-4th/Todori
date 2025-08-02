package com.mukmuk.todori.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mukmuk.todori.ui.screen.community.CommunityScreen
import com.mukmuk.todori.ui.screen.home.HomeScreen
import com.mukmuk.todori.ui.screen.mypage.MyPageScreen
import com.mukmuk.todori.ui.screen.stats.StatsScreen
import com.mukmuk.todori.ui.screen.todo.CreateCategoryScreen
import com.mukmuk.todori.ui.screen.todo.CreateStudyScreen
import com.mukmuk.todori.ui.screen.todo.TodoScreen

@Composable
fun AppNavigation(navController: NavHostController,modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Todo.route,
        modifier = modifier // 추가!
    ) {
        composable(BottomNavItem.Todo.route) { TodoScreen(navController) }
        composable(BottomNavItem.Stats.route) { StatsScreen() }
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Study.route) { CommunityScreen() }
        composable(BottomNavItem.MyPage.route) { MyPageScreen() }
        composable("category/create") {
            CreateCategoryScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable("goal/create") {
            CreateCategoryScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable("study/create") {
            CreateStudyScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}