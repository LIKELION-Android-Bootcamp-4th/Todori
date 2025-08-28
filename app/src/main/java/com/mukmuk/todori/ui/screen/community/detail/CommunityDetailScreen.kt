package com.mukmuk.todori.ui.screen.community.detail

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.data.remote.study.StudyMember
import com.mukmuk.todori.ui.screen.community.StudyCategory
import com.mukmuk.todori.ui.screen.community.components.CommunityDetailComment
import com.mukmuk.todori.ui.screen.community.components.CommunityDetailCommentReply
import com.mukmuk.todori.ui.screen.community.components.StudyDetailCard
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.ButtonPrimary
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupSecondary
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.getLevelInfo

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailScreen(
    postId: String,
    onBack: () -> Unit,
    navController: NavController,
    viewModel: CommunityDetailViewModel
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    var commentContent by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var deleteTargetCommentId by remember { mutableStateOf<String?>(null) }
    val textSet = LocalFocusManager.current
    val td by remember { mutableStateOf(textSet) }
    var dialogInfo by remember { mutableStateOf<String?>(null) }
    val uid = Firebase.auth.currentUser?.uid.toString()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.getUserById(uid)
        viewModel.loadPostById(postId)
        viewModel.setReplyToCommentId(null)
    }

    BackHandler(enabled = state.replyToCommentId != null) {
        viewModel.setReplyToCommentId(null)
        commentContent = ""
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "게시글", style = AppTextStyle.AppBar)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "이전"
                        )
                    }
                },
                actions = {
                    if (state.post?.createdBy == uid) {
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "더보기")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .background(White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Gray, RoundedCornerShape(10.dp))
                            ) {
                                viewModel.menu.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item, style = AppTextStyle.BodySmall) },
                                        onClick = {
                                            expanded = false
                                            if (item == "수정") {
                                                navController.navigate("community/create?postId=$postId")
                                            } else if (item == "삭제") {
                                                showDialog = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars),
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = White,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .height(68.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentContent,
                    onValueChange = { commentContent = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(White, RoundedCornerShape(30.dp))
                        .border(1.dp, Gray, RoundedCornerShape(30.dp)),
                    shape = RoundedCornerShape(30.dp),
                    placeholder = { Text(if(state.replyToCommentId != null) "답글을 작성해주세요" else "댓글을 작성해주세요", style = AppTextStyle.Body.copy(color = DarkGray)) },
                    singleLine = true,
                    maxLines = 1,
                )
                Button(
                    onClick = {
                        if(commentContent.isNotBlank()) {
                            if (state.replyToCommentId != null) {
                                viewModel.createCommentReply(
                                    postId,
                                    state.replyToCommentId!!, StudyPostComment(
                                        commentId = "",
                                        postId = postId,
                                        uid = uid,
                                        level = state.user?.level ?: 0,
                                        username = state.user?.nickname ?: "",
                                        content = commentContent,
                                        createdAt = Timestamp.now()
                                    )
                                )
                            } else {
                                viewModel.createComment(
                                    postId, StudyPostComment(
                                        commentId = "",
                                        postId = postId,
                                        uid = uid,
                                        level = state.user?.level ?: 0,
                                        username = state.user?.nickname ?: "",
                                        content = commentContent,
                                        createdAt = Timestamp.now()
                                    )
                                )
                            }
                            commentContent = ""
                            viewModel.getComments(postId)
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(commentContent.isNotBlank()) ButtonPrimary else Gray
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "전송", Modifier.size(24.dp))
                }
            }
        }

    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ButtonPrimary)
            }
        } else if (postId.isBlank() || state.post == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "게시글을 불러오지 못했습니다",
                    style = AppTextStyle.Body.copy(color = DarkGray, fontWeight = FontWeight.Bold)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(innerPadding)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus(force = true)
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val levelInfo = getLevelInfo(state.post?.level ?: 0)
                        Image(
                            painter = painterResource(id = levelInfo.imageRes),
                            contentDescription = "레벨 이미지",
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .border(width = 1.dp, shape = CircleShape, color = Gray)
                        )
                        Spacer(Modifier.width(Dimens.Tiny))
                        Text(
                            state.post?.userName ?: "",
                            style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            viewModel.formatDate(state.post?.createdAt),
                            style = AppTextStyle.BodySmall.copy(
                                color = DarkGray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    state.post?.title?.let {
                        Text(
                            it,
                            style = AppTextStyle.Title.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    state.post?.let {
                        Text(
                            it.content,
                            style = AppTextStyle.Body
                        )
                    }
                    Spacer(Modifier.height(Dimens.Large))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.post?.tags?.forEach { tag ->
                            val categoryColor = StudyCategory.entries.find { category ->
                                category.tags.contains(tag)
                            }?.color ?: GroupSecondary
                            Box(
                                modifier = Modifier
                                    .background(categoryColor, RoundedCornerShape(32.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .width(60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tag,
                                    style = AppTextStyle.BodySmallMedium.copy(color = White)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    if (state.post?.studyId != null && state.study != null) {
                        StudyDetailCard(
                            uid = uid,
                            study = state.study!!,
                            memberList = state.memberList,
                            selectedDate = null,
                            onClick = {
                                viewModel.updateStudyMember(
                                    postId,
                                    state.post!!.studyId,
                                    StudyMember(
                                        uid = uid,
                                        nickname = state.user?.nickname ?: "",
                                        studyId = state.post!!.studyId,
                                        role = "MEMBER",
                                        joinedAt = Timestamp.now(),
                                    )
                                )
                                viewModel.loadPostById(postId)
                            }
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Gray)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    Spacer(Modifier.height(Dimens.Large))
                    Column(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        state.commentList.forEach { comment ->
                            CommunityDetailComment(
                                uid = uid,
                                commentList = comment,
                                onReplyClick = {
                                    viewModel.setReplyToCommentId(comment.commentId)
                                    commentContent = ""
                                },
                                onDeleteClick = {
                                    showCommentDialog = true
                                    deleteTargetCommentId = comment.commentId
                                    dialogInfo = "댓글"
                                    viewModel.setReplyToCommentId(null)
                                }
                            )
                            if (state.commentReplyList.containsKey(comment.commentId)) {
                                state.commentReplyList[comment.commentId]?.forEach { reply ->
                                    CommunityDetailCommentReply(
                                        uid = uid,
                                        commentList = reply,
                                        onDeleteClick = {
                                            showCommentDialog = true
                                            deleteTargetCommentId = reply.commentId
                                            dialogInfo = "답글"
                                            viewModel.setReplyToCommentId(null)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("삭제", style = AppTextStyle.Title) },
                        text = { Text("해당 게시글을 삭제하시겠습니까?", style = AppTextStyle.Body) },
                        containerColor = White,
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    viewModel.deletePost(postId)
                                    onBack()
                                },
                            ) {
                                Text("확인", style = AppTextStyle.Body.copy(color = ButtonPrimary))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                },
                            ) {
                                Text("취소", style = AppTextStyle.Body.copy(color = ButtonPrimary))
                            }
                        }
                    )
                }
                if (showCommentDialog) {
                    AlertDialog(
                        onDismissRequest = { showCommentDialog = false },
                        title = { Text("삭제", style = AppTextStyle.Title) },
                        text = { Text("${dialogInfo}을 삭제하시겠습니까?", style = AppTextStyle.Body) },
                        containerColor = White,
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showCommentDialog = false
                                    deleteTargetCommentId?.let {
                                        viewModel.deleteComment(postId, it)
                                    }
                                },
                            ) {
                                Text("확인", style = AppTextStyle.Body.copy(color = ButtonPrimary))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showCommentDialog = false
                                },
                            ) {
                                Text("취소", style = AppTextStyle.Body.copy(color = ButtonPrimary))
                            }
                        }
                    )
                }
            }
        }
    }
}