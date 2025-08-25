package com.mukmuk.todori.ui.screen.community.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mukmuk.todori.ui.screen.community.components.CommunityListData
import com.mukmuk.todori.ui.screen.community.components.ListPickerBottomSheet
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.GroupSecondary
import com.mukmuk.todori.ui.theme.White

private val allTags = listOf("토익", "언어", "개발", "자기계발", "실습", "운동", "수학", "국어", "독서", "예체능")

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityScreen(
    postId: String? = null,
    navController: NavController,
    onBack: () -> Unit,
    viewModel: CreateCommunityViewModel
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(postId) {
        if (postId != null) {
            viewModel.onEvent(CreateCommunityEvent.LoadPostForEditing(postId))
        }
    }

    LaunchedEffect(state.isPostSubmitted) {
        if (state.isPostSubmitted) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (postId != null) "게시글 수정" else "게시글 작성",
                        style = AppTextStyle.AppBar
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
            )
        },
        containerColor = White,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = { viewModel.onEvent(CreateCommunityEvent.OnPostSubmit(postId)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = White)
                    } else {
                        Text("작성", style = AppTextStyle.Body.copy(color = White))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(CreateCommunityEvent.OnTitleChange(it)) },
                placeholder = {
                    Text("스터디 명을 입력하세요", style = AppTextStyle.Body.copy(color = DarkGray))
                },
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                isError = state.isTitleError,
                supportingText = {
                    if (state.isTitleError) Text("스터디 명을 입력해주세요", style = AppTextStyle.Body)
                }
            )

            OutlinedTextField(
                value = state.content,
                onValueChange = { viewModel.onEvent(CreateCommunityEvent.OnContentChange(it)) },
                placeholder = {
                    Text("스터디 설명을 작성 해주세요", style = AppTextStyle.Body.copy(color = DarkGray))
                },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(10.dp),
                minLines = 6
            )

            Spacer(Modifier.height(Dimens.Large))

            Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Gray))

            Spacer(Modifier.height(Dimens.Large))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("내가 만든 스터디", style = AppTextStyle.Body)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { viewModel.onEvent(CreateCommunityEvent.OnStudyPickerClick) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GroupPrimary, contentColor = Black)
                ) {
                    Text("불러오기", style = AppTextStyle.Body)
                }
            }

            Spacer(Modifier.height(Dimens.Large))

            Text("스터디 선택하기", style = AppTextStyle.Body)

            Spacer(Modifier.height(Dimens.Large))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                allTags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(
                                if (state.selectedTags.contains(tag)) GroupPrimary else GroupSecondary,
                                RoundedCornerShape(32.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable { viewModel.onEvent(CreateCommunityEvent.OnTagClick(tag)) }
                            .width(60.dp),
                    ) {
                        Text(text = tag, color = Black, style = AppTextStyle.BodySmall)
                    }
                    Spacer(modifier = Modifier.padding(16.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (state.isStudyPickerVisible) {
            ListPickerBottomSheet(
                studies = state.myStudyList,
                show = state.isStudyPickerVisible,
                onDismissRequest = { viewModel.onEvent(CreateCommunityEvent.OnStudyPickerDismiss) },
                onSelect = { studyId -> viewModel.onEvent(CreateCommunityEvent.OnStudySelected(studyId)) }
            )
        }
    }
}