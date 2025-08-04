package com.mukmuk.todori.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.screen.community.CommunityScreen
import com.mukmuk.todori.ui.screen.home.HomeScreen
import com.mukmuk.todori.ui.screen.mypage.CompletedGoalsScreen
import com.mukmuk.todori.ui.screen.mypage.MyLevelScreen
import com.mukmuk.todori.ui.screen.home.HomeViewModel
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingScreen
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingViewModel
import com.mukmuk.todori.ui.screen.mypage.MyPageScreen
import com.mukmuk.todori.ui.screen.stats.StatsScreen
import com.mukmuk.todori.ui.screen.todo.create.CreateCategoryScreen
import com.mukmuk.todori.ui.screen.todo.create.CreateGoalScreen
import com.mukmuk.todori.ui.screen.todo.create.CreateStudyScreen
import com.mukmuk.todori.ui.screen.todo.TodoScreen
import com.mukmuk.todori.ui.screen.todo.detail.GoalDetailScreen
import com.mukmuk.todori.ui.screen.todo.detail.MemberProgressDetailScreen
import com.mukmuk.todori.ui.screen.todo.detail.StudyDetailScreen
import com.mukmuk.todori.ui.screen.todo.detail.TodoDetailScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController,modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Todo.route,
        modifier = modifier // 추가!
    ) {
        composable(BottomNavItem.Todo.route) { TodoScreen(navController) }
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
        composable(BottomNavItem.MyPage.route) { MyPageScreen(navController) }
        composable("myLevel") { MyLevelScreen(onBack = {navController.popBackStack()}) }
        composable("completedGoals") { CompletedGoalsScreen(onBack = {navController.popBackStack()}) }
        composable("category/create") { backStackEntry ->
            val navEntry = navController.previousBackStackEntry
            val category = navEntry?.savedStateHandle?.get<TodoCategory>("editCategory")
            CreateCategoryScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                editCategory = category
            )
        }
        composable("goal/create") { backStackEntry ->
            val navEntry = navController.previousBackStackEntry
            val editGoal = navEntry?.savedStateHandle?.get<Goal>("goal")
            CreateGoalScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                editGoal = editGoal
            )
        }
        composable("study/create") {
            val navEntry = navController.previousBackStackEntry
            val editStudy = navEntry?.savedStateHandle?.get<Study>("editStudy")
            CreateStudyScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                editStudy = editStudy
            )
        }
        composable("todo/detail/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            TodoDetailScreen(categoryId = categoryId, navController = navController, onBack = { navController.popBackStack() })
        }
        composable("goal/detail") { backStackEntry ->
            val goal = navController.previousBackStackEntry
                ?.savedStateHandle?.get<Goal>("goal")
            goal?.let {
                GoalDetailScreen(goal = it, navController = navController,
                    onBack = {navController.popBackStack()})
            }
        }
        composable("study/detail") {
            StudyDetailScreen(
                navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable("member_progress_detail/{studyId}") { backStackEntry ->
            val studyId = backStackEntry.arguments?.getString("studyId") ?: ""
            MemberProgressDetailScreen(
                navController = navController,
                studyId = studyId,
            )
        }


    }
}