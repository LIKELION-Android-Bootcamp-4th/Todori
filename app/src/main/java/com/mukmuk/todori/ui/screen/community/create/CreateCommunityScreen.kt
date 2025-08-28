package com.mukmuk.todori.ui.screen.community.create

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mukmuk.todori.ui.screen.community.components.CommunityListData
import com.mukmuk.todori.ui.screen.community.components.ListPickerBottomSheet
import com.mukmuk.todori.ui.screen.community.components.TagPickerBottomSheet
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.GroupSecondary
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityScreen(
    postId: String? = null,
    navController: NavController,
    onBack: () -> Unit,
    viewModel: CreateCommunityViewModel
) {
    val context = LocalContext.current
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
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(CreateCommunityEvent.OnToastShown)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(10.dp),
                minLines = 6
            )

            Spacer(Modifier.height(Dimens.Large))

            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Gray)
            )

            Spacer(Modifier.height(Dimens.Large))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("태그 (${state.selectedTags.size}/3)", style = AppTextStyle.Body)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { viewModel.onEvent(CreateCommunityEvent.OnTagPickerClick) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(DefaultCornerRadius),
                    border = BorderStroke(1.dp, Gray),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                        .padding(horizontal = Dimens.Nano)
                ) {
                    Text(
                        "태그 선택",
                        style = AppTextStyle.BodySmallMedium
                    )
                }
            }
            if (state.selectedTags.isNotEmpty()) {
                Spacer(Modifier.height(Dimens.Small))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.selectedTags.forEach { tag ->
                        InputChip(
                            onClick = {},
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(tag, style = AppTextStyle.BodySmallMedium)
                                    IconButton(
                                        onClick = {
                                            viewModel.onEvent(CreateCommunityEvent.OnTagClicked(tag))
                                        },
                                        modifier = Modifier.size(24.dp) // 아이콘 크기 조정
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove tag",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            selected = true,
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = GroupPrimary.copy(alpha = 0.1f),
                                selectedLabelColor = GroupPrimary
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.height(Dimens.Large))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("스터디", style = AppTextStyle.Body)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { viewModel.onEvent(CreateCommunityEvent.OnStudyPickerClick) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    shape = RoundedCornerShape(DefaultCornerRadius),
                    border = BorderStroke(1.dp, Gray),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                        .padding(horizontal = Dimens.Nano)
                ) {
                    Text(
                        "스터디 선택",
                        style = AppTextStyle.BodySmallMedium
                    )
                }
            }
            state.currentStudy?.let { study ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.Small)
                ) {
                    CommunityListData(
                        title = study.studyName,
                        description = study.description,
                        createdAt = study.createdAt,
                        memberCount = null,
                        activeDays = study.activeDays,
                        onClick = {},
                    )

                    IconButton(
                        onClick = { viewModel.onEvent(CreateCommunityEvent.OnStudyDeselected) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "스터디 선택 취소",
                            tint = DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        if (state.isStudyPickerVisible) {
            ListPickerBottomSheet(
                studies = state.myStudyList.filter { !it.hasPosted },
                show = state.isStudyPickerVisible,
                onDismissRequest = { viewModel.onEvent(CreateCommunityEvent.OnStudyPickerDismiss) },
                onSelect = { studyId ->
                    viewModel.onEvent(
                        CreateCommunityEvent.OnStudySelected(
                            studyId
                        )
                    )
                }
            )
        }
        if (state.isTagPickerVisible) {
            TagPickerBottomSheet(
                show = state.isTagPickerVisible,
                onDismissRequest = { viewModel.onEvent(CreateCommunityEvent.OnTagPickerDismiss) },
                selectedTags = state.selectedTags,
                onTagClick = { tag -> viewModel.onEvent(CreateCommunityEvent.OnTagClicked(tag)) }
            )
        }
    }
}