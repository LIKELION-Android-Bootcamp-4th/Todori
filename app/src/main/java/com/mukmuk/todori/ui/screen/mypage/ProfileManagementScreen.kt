package com.mukmuk.todori.ui.screen.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White

@Composable
fun ProfileManagementScreen(
    onBack: () -> Unit,
    onDone: (nickname: String, intro: String) -> Unit,
) {
    //테스트 유저
    val currentUser = User(
        uid = "123",
        nickname = "asd",
        intro = "asdasd",
        level = 3,
        rewardPoint = 1200
    )

    var nickname by remember {
        mutableStateOf(
            TextFieldValue(
                text =currentUser.nickname,
                selection = TextRange(currentUser.nickname.length)
            )
        )
    }
    var intro by remember {
        mutableStateOf(
            TextFieldValue(
                text = currentUser.intro.orEmpty(),
                selection = TextRange(currentUser.intro?.length ?: 0)
            )
        )
    }

    val nicknameFocusRequester = remember { FocusRequester() }
    val introFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val isNicknameError = nickname.text.length !in 0..8
    val isIntroError = intro.text.length !in 0..12

    LaunchedEffect(Unit) {
        nicknameFocusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(title = "프로필 관리", onBackClick = onBack)
        },
        containerColor = White
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(Dimens.Medium)
                .background(color = White)
        ) {
            Spacer(modifier = Modifier.height(Dimens.Medium))

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("닉네임") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nicknameFocusRequester),
                shape = RoundedCornerShape(DefaultCornerRadius),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        introFocusRequester.requestFocus()
                    }
                ),
                isError = isNicknameError,
                supportingText = {
                    if (isNicknameError) Text("1~8자 사이로 입력해주세요.") else null
                }
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            OutlinedTextField(
                value = intro,
                onValueChange = { intro = it },
                label = { Text("한줄 소개") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(introFocusRequester),
                shape = RoundedCornerShape(DefaultCornerRadius),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                isError = isIntroError,
                supportingText = {
                    if (isIntroError) Text("1~12자 사이로 입력해주세요.") else null
                }
            )

            Button(
                onClick = {
                    if (isNicknameError || isIntroError) return@Button

                    onDone(nickname.text, intro.text)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("완료")
            }
        }
    }
}