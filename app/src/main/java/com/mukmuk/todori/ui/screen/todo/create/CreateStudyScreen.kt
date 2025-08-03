package com.mukmuk.todori.ui.screen.todo.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.study.Study
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.White

@Composable
fun CreateStudyScreen(
    onDone: () -> Unit,
    onBack: () -> Unit,
    editStudy: Study? = null
) {
    val isEditMode =
        editStudy !=null

    var title by remember { mutableStateOf(editStudy?.title.orEmpty()) }
    var description by remember { mutableStateOf(editStudy?.description.orEmpty()) }
    val selectedDays = remember {
        mutableStateListOf<String>().apply { editStudy?.activeDays?.let { addAll(it) } }
    }

    val titleFocusRequester = remember { FocusRequester() }
    val descFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val isTitleError = title.length !in 0..20
    val isDescError = description.length !in 0..60

    val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                if (isEditMode) "스터디 수정" else "스터디 생성",
                onBackClick = onBack)
        },
        containerColor = White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(Dimens.Medium)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("스터디 제목") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(titleFocusRequester),
                shape = RoundedCornerShape(DefaultCornerRadius),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    descFocusRequester.requestFocus()
                }),
                isError = isTitleError,
                supportingText = {
                    if (isTitleError) Text("1~20자 사이로 입력해주세요.")
                }
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("스터디 설명") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descFocusRequester),
                shape = RoundedCornerShape(DefaultCornerRadius),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                }),
                isError = isDescError,
                supportingText = {
                    if (isDescError) Text("1~60자 사이로 입력해주세요.")
                }
            )

            Spacer(modifier = Modifier.height(Dimens.Medium))

            Text("반복 요일", style = AppTextStyle.Body)

            Spacer(modifier = Modifier.height(Dimens.Small))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .border(1.dp, DarkGray, CircleShape)
                    .padding(Dimens.Nano),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                dayLabels.forEach { label ->
                    val isSelected = label in selectedDays

                    Box(
                        modifier = Modifier
                            .padding(horizontal= Dimens.Nano)
                            .clip(CircleShape)
                            .background(if (isSelected) GroupPrimary else Color.Transparent)
                            .clickable {
                                if (isSelected) selectedDays.remove(label)
                                else selectedDays.add(label)
                            }
                            .padding(horizontal = Dimens.Medium, vertical = Dimens.Tiny),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = AppTextStyle.BodySmall,
                            color = if (isSelected) White else Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isEditMode) {
                        // TODO 수정
                    } else {
                        // Todo 생성
                    }

                    onDone()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("완료")
            }
        }
    }
}
