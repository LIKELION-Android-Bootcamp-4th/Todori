package com.mukmuk.todori.ui.screen.todo

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.mukmuk.todori.data.remote.goal.Goal
import com.mukmuk.todori.ui.component.SimpleTopAppBar
import com.mukmuk.todori.ui.screen.todo.component.DateRangePicker
import com.mukmuk.todori.ui.screen.todo.component.DateRangePickerBottomSheet
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateGoalScreen(
    onDone: () -> Unit,
    onBack: () -> Unit,
    editGoal: Goal? = null
) {
    val isEditMode = editGoal != null

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var title by remember { mutableStateOf(editGoal?.title.orEmpty()) }
    var description by remember { mutableStateOf(editGoal?.description.orEmpty()) }
    var startDate by remember {
        mutableStateOf(
            editGoal?.startDate?.takeIf { it.isNotBlank() }?.let {
                LocalDate.parse(it, formatter)
            }
        )
    }
    var endDate by remember {
        mutableStateOf(
            editGoal?.endDate?.takeIf { it.isNotBlank() }?.let {
                LocalDate.parse(it, formatter)
            }
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }

    val titleFocusRequester = remember { FocusRequester() }
    val descFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val isTitleError = title.length !in 0..15       // 목표  1~15
    val isDescError = description.length !in 0..60 // 설명  1~60
    val isDateError = startDate == null || endDate == null

    val dateFormatter = DateTimeFormatter.ofPattern("yy.MM.dd")
    val dateRangeText = if (startDate != null && endDate != null)
        "${startDate!!.format(dateFormatter)} - ${endDate!!.format(dateFormatter)}"
    else "기간 선택"

    LaunchedEffect(Unit) {
        titleFocusRequester.requestFocus()
    }


    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = if (isEditMode) "목표 수정" else "목표 생성",
                onBackClick = onBack
            )
        },
        containerColor = White
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(Dimens.Medium).background(color = White)
        ) {
            Spacer(modifier = Modifier.height(Dimens.Medium))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("목표") },
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
                    if (isTitleError) Text("1~15자 사이로 입력해주세요.") else null
                }
            )

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("설명") },
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

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            DateRangePicker(
                dateRangeText = dateRangeText,
                onClick = { showDatePicker = true }
            )

            DateRangePickerBottomSheet(
                show = showDatePicker,
                onDismissRequest = { showDatePicker = false },
                onConfirm = { start, end ->
                    startDate = start
                    endDate = end
                }
            )


            if (isDateError) {
                Text("시작일과 종료일을 선택해주세요.", color = Red, style = AppTextStyle.BodySmall)
                Spacer(modifier = Modifier.height(Dimens.Tiny))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isEditMode) {
                        // TODO: 수정 API 호출
                    } else {
                        // TODO: 생성 API 호출
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
