package com.mukmuk.todori.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mukmuk.todori.navigation.BottomNavItem
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController
) {
    val state = viewModel.state


    // 상태 변화 감지 후 이동 처리
    LaunchedEffect(state.status) {
        if (state.status == LoginStatus.SUCCESS) {
            navController.navigate(BottomNavItem.Todo.route) {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // 로고나 이미지
//            Image(
//                painter = painterResource(id = R.drawable.fire_squirrel),
//                contentDescription = "Fire Squirrel",
//                modifier = Modifier.size(200.dp)
//            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    viewModel.onEvent(LoginEvent.KakaoLogin)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("카카오로 시작하기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.onEvent(LoginEvent.GoogleLogin)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Google로 시작하기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.onEvent(LoginEvent.NaverLogin)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Naver로 시작하기")
            }

            Spacer(modifier = Modifier.height(Dimens.XXLarge))

            Text(
                text = "계속 진행 시 이용약관 동의 및 개인정보 처리방침 확인으로 간주합니다.",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
