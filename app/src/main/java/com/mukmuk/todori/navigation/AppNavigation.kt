package com.mukmuk.todori.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.screen.community.CommunityScreen
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import com.mukmuk.todori.ui.screen.community.create.CreateCommunityScreen
import com.mukmuk.todori.ui.screen.community.create.CreateCommunityViewModel
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailScreen
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailViewModel
import com.mukmuk.todori.ui.screen.community.search.CommunitySearchScreen
import com.mukmuk.todori.ui.screen.home.HomeScreen
import com.mukmuk.todori.ui.screen.home.HomeViewModel
import com.mukmuk.todori.ui.screen.home.home_ocr.HomeOcrScreen
import com.mukmuk.todori.ui.screen.home.home_setting.HomeSettingScreen
import com.mukmuk.todori.ui.screen.login.LoginScreen
import com.mukmuk.todori.ui.screen.mypage.CompletedGoalsScreen
import com.mukmuk.todori.ui.screen.mypage.MyLevelScreen
import com.mukmuk.todori.ui.screen.mypage.MyPageScreen
import com.mukmuk.todori.ui.screen.mypage.ProfileManagementScreen
import com.mukmuk.todori.ui.screen.mypage.studytargets.StudyTargetsScreen
import com.mukmuk.todori.ui.screen.splash.SplashScreen
import com.mukmuk.todori.ui.screen.stats.MonthlyReportScreen
import com.mukmuk.todori.ui.screen.stats.StatsScreen
import com.mukmuk.todori.ui.screen.todo.TodoScreen
import com.mukmuk.todori.ui.screen.todo.create.CreateCategoryScreen
import com.mukmuk.todori.ui.screen.todo.create.CreateGoalScreen
import com.mukmuk.todori.ui.screen.todo.create.CreateStudyScreen
import com.mukmuk.todori.ui.screen.todo.detail.MemberProgressDetailScreen
import com.mukmuk.todori.ui.screen.todo.detail.goal.GoalDetailScreen
import com.mukmuk.todori.ui.screen.todo.detail.study.StudyDetailScreen
import com.mukmuk.todori.ui.screen.todo.detail.study.StudyDetailViewModel
import com.mukmuk.todori.ui.screen.todo.detail.todo.SendTodoDetailScreen
import com.mukmuk.todori.ui.screen.todo.detail.todo.TodoDetailScreen
import kotlinx.datetime.LocalDate

@SuppressLint("ComposableDestinationInComposeScope")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") { SplashScreen(navController) }

        composable(
            route = BottomNavItem.Todo.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "todori://app.todori.com/todo" }
            )
        ) { TodoScreen(navController) }

        composable(
            BottomNavItem.Todo.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://todori-7d791.web.app/category?categoryId={categoryId}"
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            TodoScreen(navController, categoryId = categoryId)
        }

        composable(
            route = BottomNavItem.Stats.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "todori://app.todori.com/stats" }
            )
        ) { StatsScreen(navController) }
        composable(
            route = BottomNavItem.Home.route,
            deepLinks = listOf(navDeepLink { uriPattern = "todori://app.todori.com/home" })
        ) {
            HomeScreen(navController = navController, viewModel = homeViewModel)
        }
        composable("home_setting") { HomeSettingScreen(navController = navController) }
        composable("home_ocr") { HomeOcrScreen(navController) }

        composable(BottomNavItem.Study.route) { backStackEntry ->

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BottomNavItem.Study.route)
            }
            val viewModel: CommunityViewModel = hiltViewModel(parentEntry)
            CommunityScreen(navController, viewModel)
        }
        composable(
            route = "community/create?postId={postId}",
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val viewModel: CreateCommunityViewModel = hiltViewModel()
            val postId = backStackEntry.arguments?.getString("postId")
            CreateCommunityScreen(
                postId,
                navController,
                onBack = { navController.popBackStack() },
                viewModel
            )
        }
        composable("community/search") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BottomNavItem.Study.route)
            }
            val viewModel: CommunityViewModel = hiltViewModel(parentEntry)
            CommunitySearchScreen(
                onBack = { navController.popBackStack() },
                navController,
                viewModel
            )
        }
        composable(
            route = "community/detail/{postId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "todori://app.todori.com/community/detail/{postId}" }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(BottomNavItem.Study.route)
            }
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val viewModel: CommunityDetailViewModel = hiltViewModel(parentEntry)
            CommunityDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() },
                navController,
                viewModel,
            )
        }

        composable(BottomNavItem.MyPage.route) { MyPageScreen(navController) }
        composable("myLevel") { MyLevelScreen(onBack = { navController.popBackStack() }) }
        composable("completedGoals") { CompletedGoalsScreen(onBack = { navController.popBackStack() }) }
        composable("profileManage") {
            ProfileManagementScreen(onBack = { navController.popBackStack() })
        }

        composable("category/create") {
            val navEntry = navController.previousBackStackEntry
            val category = navEntry?.savedStateHandle?.get<TodoCategory>("editCategory")
            CreateCategoryScreen(
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                editCategory = category
            )
        }
        composable(
            route = "login",
            deepLinks = listOf(
                navDeepLink { uriPattern = "todori://app.todori.com/login" }
            )) {
            LoginScreen(navController = navController)
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

        composable(
            "todo/detail/{categoryId}?date={date}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            TodoDetailScreen(
                categoryId = categoryId,
                date = date,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "sendTodo/detail/{categoryId}?date={date}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            SendTodoDetailScreen(
                categoryId = categoryId,
                date = date,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "goal/detail/{goalId}?date={date}",
            arguments = listOf(
                navArgument("goalId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "todori://app.todori.com/goal/detail/{goalId}?date={date}"
                }
            )
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            GoalDetailScreen(
                goalId = goalId,
                navController = navController,
                selectedDate = date,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "study/detail/{studyId}?date={date}",
            arguments = listOf(
                navArgument("studyId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "todori://app.todori.com/study/detail/{studyId}?date={date}"
                }
            )
        ) { backStackEntry ->
            val studyId = backStackEntry.arguments?.getString("studyId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            StudyDetailScreen(
                navController = navController,
                studyId = studyId,
                selectedDate = date,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            "member_progress_detail/{studyId}?date={date}",
            arguments = listOf(
                navArgument("studyId") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val studyId = backStackEntry.arguments?.getString("studyId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("study/detail/$studyId?date=$date")
            }
            val viewModel: StudyDetailViewModel = hiltViewModel(parentEntry)
            MemberProgressDetailScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("studyTargets") { StudyTargetsScreen(navController = navController) }

        composable(
            route = "monthly_report/{uid}/{date}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            val dateStr = backStackEntry.arguments?.getString("date") ?: ""
            val date = LocalDate.parse(dateStr)

            MonthlyReportScreen(
                uid = uid,
                date = date,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}