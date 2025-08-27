package com.mukmuk.todori.ui.screen.login

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mukmuk.todori.R
import com.mukmuk.todori.navigation.BottomNavItem
import com.mukmuk.todori.ui.screen.login.components.LoginButton
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonKakao
import com.mukmuk.todori.ui.theme.ButtonNaver
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(
    navController: NavController
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state = viewModel.state
    val context = LocalContext.current
    val activity = LocalContext.current as Activity


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleSignInResult(result.data)
    }

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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_level3_removebg),
                        contentDescription = "앱 로고",
                        modifier = Modifier.size(160.dp)
                    )
                    Spacer(modifier = Modifier.height(Dimens.Small))
                    Text(
                        text = "Todori",
                        style = AppTextStyle.Timer
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LoginButton(
                        imageRes = R.drawable.leader,
                        text = "Test 계정으로 로그인",
                        textColor = Black,
                        backgroundColor = White,
                        onClick = {
                            viewModel.loginWithTestAccount(
                                onSuccess = {
                                    Log.d("Login", "테스트 계정 로그인 성공")
                                    navController.navigate("home")
                                },
                                onError = { Log.e("Login", it) }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LoginButton(
                        imageRes = R.drawable.ic_google_logo,
                        text = "Google 계정으로 로그인",
                        textColor = Black,
                        backgroundColor = White,
                        onClick = {
                            launcher.launch(viewModel.getGoogleSignInIntent(context))
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LoginButton(
                        imageRes = R.drawable.ic_naver_logo,
                        text = "네이버로 시작하기",
                        textColor = White,
                        backgroundColor = ButtonNaver,
                        onClick = {
                            viewModel.naverLogin(activity)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LoginButton(
                        imageRes = R.drawable.ic_kakao_logo,
                        text = "카카오로 시작하기",
                        textColor = Black,
                        backgroundColor = ButtonKakao,
                        onClick = {
                            viewModel.kakaoLogin(context)
                        }
                    )
                }
            }
        }
    }
}