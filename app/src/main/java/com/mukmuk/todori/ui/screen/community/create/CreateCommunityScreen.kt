package com.mukmuk.todori.ui.screen.community.create

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import com.mukmuk.todori.ui.screen.community.components.CommunityListData
import com.mukmuk.todori.ui.screen.community.components.ListPickerBottomSheet
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailViewModel
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
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
    viewModel: CommunityDetailViewModel
) {
    val scrollState = rememberScrollState()

    val state by viewModel.state.collectAsState()

    var title by remember { mutableStateOf("") }
    var isTitleError by remember { mutableStateOf(false) }

    var content by remember { mutableStateOf("") }

    var data = listOf("토익", "언어", "개발", "자기계발", "실습", "운동", "수학", "국어", "독서", "예체능")

    var asd = listOf("")

    var showListSheet by remember { mutableStateOf(false) }
    var pickedItem by remember { mutableStateOf<String?>(null) }

    var studyId by remember { mutableStateOf("") }

    val td = remember { mutableStateListOf<String>() }

    val uid = Firebase.auth.currentUser?.uid.toString()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(postId) {
        viewModel.getUserById(uid)
        if (postId == null) {
            title = ""
            content = ""
            studyId = ""
            td.clear()
        } else {
            viewModel.loadPostById(postId)
        }
    }

    LaunchedEffect(postId, state.post) {
        if (postId != null && state.post?.postId == postId) {
            val post = state.post!!
            title = post.title
            content = post.content
            studyId = post.studyId
            td.clear()
            td.addAll(post.tags.distinct())
        }
    }

    Scaffold(

        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if(postId != null)
                    {
                        Text("게시글 수정", style = AppTextStyle.AppBar)
                    }
                    else {
                        Text("게시글 작성", style = AppTextStyle.AppBar)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier.height(56.dp).fillMaxWidth(),
            )
        },

        containerColor = White,

        contentWindowInsets = WindowInsets(0.dp),

        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ){
                Button(
                    onClick = {
                        if (pickedItem != null) {
                            studyId = pickedItem!!
                            viewModel.loadStudyById(studyId)
                        }
                        if (title.isNotBlank()) {
                            if (postId != null) {
                                viewModel.updatePost(
                                    postId,
                                    StudyPost(
                                        title = title,
                                        content = content,
                                        tags = td.toList(),
                                        postId = postId,
                                        studyId = studyId,
                                        memberCount = state.post?.memberCount ?: 0,
                                        commentsCount = state.post?.commentsCount ?: 0,
                                        createdAt = state.post?.createdAt,
                                        createdBy = uid
                                    )
                                )
                            }
                            else {
                                viewModel.createPost(
                                    StudyPost(
                                        title = title,
                                        content = content,
                                        tags = td.toList(),
                                        postId = "",
                                        studyId = studyId,
                                        memberCount = state.memberList.size,
                                        commentsCount = 0,
                                        createdAt = Timestamp.now(),
                                        createdBy = uid
                                    )
                                )
                            }

                            navController.popBackStack()
                        } else {
                            isTitleError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("작성", style = AppTextStyle.Body.copy(color = White))
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
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus(force = true)
                }
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    isTitleError = it.isBlank()
                },
                placeholder = {
                    Text(
                        "스터디 명을 입력하세요",
                        style = AppTextStyle.Body.copy(color = DarkGray)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,

                isError = isTitleError,
                supportingText = {
                    if (isTitleError) Text("스터디 명을 입력해주세요", style = AppTextStyle.Body)
                }
            )



            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it

                },
                placeholder = {
                    Text(
                        "스터디 설명을 작성 해주세요",
                        style = AppTextStyle.Body.copy(color = DarkGray)
                    )
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
                Text("내가 만든 스터디", style = AppTextStyle.Body)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = {
                        showListSheet = true
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GroupPrimary,
                        contentColor = Black
                    ),

                    ) {
                    Text("불러오기", style = AppTextStyle.Body)
                }
            }

            if (studyId.isNotBlank()) {
                viewModel.loadStudyById(studyId)

                val memberCount = state.memberList.size

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    state.study?.activeDays?.let {
                        CommunityListData(
                            studyId = studyId,
                            study = state.study!!,
                            memberCount = memberCount,
                            activeDays = it,
                            onClick = {}
                        )
                    }
                }
            }

            Spacer(Modifier.height(Dimens.Large))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                data.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(
                                if (td.contains(tag)) GroupPrimary else GroupSecondary,
                                RoundedCornerShape(32.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable {
                                if (!td.contains(tag) && td.size < 3 ) {

                                    td.add(tag)

                                } else if (td.contains(tag)) {
                                    td.remove(tag)
                                }
                            }
                            .width(60.dp),
                    ) {
                        Text(
                            text = tag,
                            color = Black,
                            style = AppTextStyle.BodySmall
                        )
                    }

                    Spacer(modifier = Modifier.padding(16.dp))

                }
            }

            Spacer(modifier = Modifier.weight(1f))

        }

        if (showListSheet) {
            ListPickerBottomSheet(
                studyId = studyId,
                show = showListSheet,
                onDismissRequest = { showListSheet = false },
                onSelect = {
                    viewModel.loadStudyById(studyId = it)
                    pickedItem = it
                    showListSheet = false
                }
            )
        }

    }

}