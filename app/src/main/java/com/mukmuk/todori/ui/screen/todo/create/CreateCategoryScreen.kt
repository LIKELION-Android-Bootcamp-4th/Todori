package com.mukmuk.todori.ui.screen.todo.create

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.mukmuk.todori.data.remote.todo.TodoCategory
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.White

@Composable
fun CreateCategoryScreen(
    onDone: () -> Unit,
    onBack: () -> Unit,
    editCategory: TodoCategory? = null
) {
    val isEditMode =
        editCategory !=null

    var title by remember { mutableStateOf(editCategory?.name.orEmpty()) }
    var description by remember { mutableStateOf(editCategory?.description.orEmpty()) }

    val titleFocusRequester = remember { FocusRequester() }
    val descFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val isTitleError =  title.length !in 0..8       // 제목은 1~8
    val isDescError = description.length !in 0..60 // 설명  1~60

    val viewModel: TodoCategoryViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        titleFocusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = if (isEditMode) "카테고리 수정" else "개인 카테고리 생성",
                onBackClick = onBack
            )
        },
        containerColor = White
    ) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(top = innerPadding.calculateTopPadding())
            .padding(Dimens.Medium).
            background(color = White)
        ) {
            Spacer(modifier = Modifier.height(Dimens.Medium))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth().focusRequester(titleFocusRequester),
                shape = RoundedCornerShape(DefaultCornerRadius),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        descFocusRequester.requestFocus()
                    }
                ),
                isError = isTitleError,
                supportingText = {
                    if (isTitleError) Text("1~8자 사이로 입력해주세요.") else null
                }
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("내용") },
                modifier = Modifier.fillMaxWidth().focusRequester(descFocusRequester),
                shape = RoundedCornerShape(DefaultCornerRadius),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                isError = isDescError,
                supportingText = {
                    if (isDescError) Text("1~60자 사이로 입력해주세요.") else null
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    Log.d("TodoCategoryService", "버튼 클릭됨")
                    if (isTitleError || isDescError) return@Button

                    if (isEditMode) {
                        // TODO: 수정
                    } else {
                        // TODO: 생성
                        val newCategory = TodoCategory(
                            categoryId = "",
                            uid = "testuser",
                            name = title,
                            colorHex = "adfa",
                            description = description,
                            createdAt = Timestamp.now()
                        )
                        viewModel.createCategory("testuser", newCategory,
                            onSuccess = {
                                Toast.makeText(context,"카테고리 생성이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                                onDone()
                            },
                            onError = { e ->
                                // 에러시 토스트 등 처리
                                Log.e("TodoCategoryService", "카테고리 생성 실패", e)
                            }
                        )
                    }

//                    onDone()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("완료")
            }
        }
    }
}