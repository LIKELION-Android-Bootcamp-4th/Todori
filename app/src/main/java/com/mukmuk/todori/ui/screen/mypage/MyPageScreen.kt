package com.mukmuk.todori.ui.screen.mypage

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.mypage.component.ProfileSection
import com.mukmuk.todori.ui.screen.mypage.component.MyPageAccountSection
import com.mukmuk.todori.ui.screen.mypage.component.MyPageMenuSection
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.White

@Composable
fun MyPageScreen(
    navController: NavController,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) viewModel.loadProfile(uid) else {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.LoggedOut -> {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is ProfileEffect.DeleteSuccess -> {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is ProfileEffect.NeedsReauth -> {
                    Toast.makeText(context, "다시 로그인 후 탈퇴를 진행하세요.", Toast.LENGTH_SHORT).show()
                }
                is ProfileEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(Dimens.Medium)
        ) {
            state.user?.let { ProfileSection(user = it) }

            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            MyPageMenuSection(
                onLevelClick = {navController.navigate("myLevel")},
                onGoalClick = {navController.navigate("completedGoals")},
                onProfileEditClick = {navController.navigate("profileManage")},
            )

            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            MyPageAccountSection(
                onLogoutClick = {
                    showLogoutDialog = true
                },
                onDeleteAccountClick = {
                    showDeleteDialog = true
                },
                onStudyTargetsClick = {navController.navigate("studyTargets")}
            )
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            containerColor = White,
            onDismissRequest = {
                if (!state.isDeleting) showDeleteDialog = false
            },
            title = { Text("회원 탈퇴") },
            text = {
                Text(
                    "정말 탈퇴하시겠어요?\n이 작업은 되돌릴 수 없어요."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !state.isDeleting,
                    onClick = {
                        if (!state.isDeleting) {
                            viewModel.deleteAccount()
                        }
                    }
                ) {
                    Text(if (state.isDeleting) "탈퇴 중..." else "탈퇴")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !state.isDeleting,
                    onClick = { showDeleteDialog = false }
                ) { Text("취소", color = Red) }
            }
        )
    }
    if (showLogoutDialog) {
        AlertDialog(
            containerColor = White,
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("로그아웃") },
            text = { Text("정말 로그아웃 하시겠어요?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        showLogoutDialog = false
                    }
                ) {
                    Text("로그아웃")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("취소", color = Red)
                }
            }
        )
    }
}
