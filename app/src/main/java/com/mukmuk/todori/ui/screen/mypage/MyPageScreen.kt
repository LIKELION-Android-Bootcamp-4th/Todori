package com.mukmuk.todori.ui.screen.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.ui.mypage.component.ProfileSection
import com.mukmuk.todori.ui.screen.mypage.component.MyPageAccountSection
import com.mukmuk.todori.ui.screen.mypage.component.MyPageMenuSection
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun MyPageScreen(
    navController: NavController,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val profile by viewModel.profile.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile("testuser")
        //viewModel.loadProfile(uid)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(Dimens.Medium)
        ) {
            profile?.let { ProfileSection(user = it) }

            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            MyPageMenuSection(
                onLevelClick = {navController.navigate("myLevel")},
                onGoalClick = {navController.navigate("completedGoals")},
                onProfileEditClick = {navController.navigate("profileManage")}
            )

            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            MyPageAccountSection(
                onLogoutClick = {
                    navController.navigate("login")
                },
                onDeleteAccountClick = {}
            )
        }
    }
}
