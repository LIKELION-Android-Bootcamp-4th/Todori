package com.mukmuk.todori.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.screen.community.CommunityScreen
import com.mukmuk.todori.ui.screen.home.HomeScreen
import com.mukmuk.todori.ui.screen.mypage.MyPageScreen
import com.mukmuk.todori.ui.screen.stats.StatsScreen
import com.mukmuk.todori.ui.screen.todo.CreateCategoryScreen
import com.mukmuk.todori.ui.screen.todo.CreateGoalScreen
import com.mukmuk.todori.ui.screen.todo.CreateStudyScreen
import com.mukmuk.todori.ui.screen.todo.TodoScreen
import com.mukmuk.todori.ui.screen.todo.detail.TodoDetailScreen

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
        composable("category/create") { backStackEntry ->
            val navEntry = navController.previousBackStackEntry
            val category = navEntry?.savedStateHandle?.get<TodoCategory>("editCategory")
            CreateCategoryScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                editCategory = category
            )
        }
        composable("goal/create") {
            CreateGoalScreen(
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
        composable("todo/detail/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            TodoDetailScreen(categoryId = categoryId, navController = navController, onBack = { navController.popBackStack() })
        }

    }
}