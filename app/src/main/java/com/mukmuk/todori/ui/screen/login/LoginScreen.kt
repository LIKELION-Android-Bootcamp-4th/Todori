package com.mukmuk.todori.ui.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mukmuk.todori.navigation.BottomNavItem

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    navController: NavController
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state = viewModel.state
    val context = LocalContext.current
    val activity = LocalContext.current as Activity


    // 구글 로그인 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

    // 상태 변화 시 네비게이션 / 에러 처리
    LaunchedEffect(state.status) {
        when (state.status) {
            LoginStatus.SUCCESS -> {
                navController.navigate(BottomNavItem.Todo.route) {
                    popUpTo("login") { inclusive = true }
                }
            }
            LoginStatus.ERROR -> {
                Toast.makeText(context, state.errorMessage ?: "로그인 실패", Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    when (state.status) {
        LoginStatus.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google
                    Button(
                        onClick = {
                            launcher.launch(viewModel.getGoogleSignInIntent(context))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) { Text("Google로 시작하기") }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Kakao
                    Button(
                        onClick = {
                            viewModel.kakaoLogin(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) { Text("카카오로 시작하기") }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Naver
                    Button(
                        onClick = {
                            viewModel.naverLogin(activity)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) { Text("Naver로 시작하기") }
                }
            }
        }
    }
}