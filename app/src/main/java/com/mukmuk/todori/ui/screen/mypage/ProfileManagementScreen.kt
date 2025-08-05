package com.mukmuk.todori.ui.screen.mypage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White

@Composable
fun ProfileManagementScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    var nickname by remember {
        mutableStateOf(
            TextFieldValue(
                text = profile?.nickname.orEmpty(),
                selection = TextRange(profile?.nickname?.length ?: 0)
            )
        )
    }
    var intro by remember {
        mutableStateOf(
            TextFieldValue(
                text = profile?.intro.orEmpty(),
                selection = TextRange(profile?.intro?.length ?: 0)
            )
        )
    }

    LaunchedEffect(profile) {
        profile?.let {
            nickname = TextFieldValue(
                text = it.nickname,
                selection = TextRange(it.nickname.length)
            )
            intro = TextFieldValue(
                text = it.intro.orEmpty(),
                selection = TextRange(it.intro?.length ?: 0)
            )
        }
    }


    val nicknameFocusRequester = remember { FocusRequester() }
    val introFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val isNicknameError = nickname.text.length !in 1..8
    val isIntroError = intro.text.length !in 1..12

    LaunchedEffect(Unit) {
        viewModel.loadProfile("testuser")
        //viewModel.loadProfile(uid)
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
                    onNext = { introFocusRequester.requestFocus() }
                ),
                isError = isNicknameError,
                supportingText = {
                    if (isNicknameError) Text("1~8자 사이로 입력해주세요.")
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
                    onDone = { focusManager.clearFocus() }
                ),
                isError = isIntroError,
                supportingText = {
                    if (isIntroError) Text("1~12자 사이로 입력해주세요.")
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isNicknameError || isIntroError) return@Button

                    viewModel.updateProfile("testuser", nickname.text, intro.text) {
                        Toast.makeText(context, "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("완료")
            }
        }
    }
}
