package com.mukmuk.todori.ui.screen.community.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.mukmuk.todori.data.remote.community.StudyPostComment
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

    var dialogInfo by remember { mutableStateOf<String?>(null) }

    val uid = Firebase.auth.currentUser?.uid.toString()


    LaunchedEffect(Unit) {
        viewModel.getUserById(uid)
        viewModel.loadPostById(postId)
        viewModel.getComments(postId)
        viewModel.setReplyToCommentId(null)


    }


    Scaffold(

        topBar = {
            TopAppBar(
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
                                modifier = Modifier
                                    .background(White, RoundedCornerShape(10.dp))
                                    .border(1.dp, Gray)
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
            )
        },

        bottomBar = {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentContent,
                    onValueChange = { commentContent = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .background(White, RoundedCornerShape(10.dp))
                        .border(1.dp, Gray, RoundedCornerShape(10.dp))
                        .onFocusChanged {
                            if(!it.isFocused){
                                viewModel.setReplyToCommentId(null)
                            }
                        },
                    shape = RoundedCornerShape(10.dp),
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
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonPrimary
                    ),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "전송")
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
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Gray, CircleShape)
                    )
                    Spacer(Modifier.width(Dimens.Tiny))
                    Text(state.post?.userName ?: "", style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))

                    Spacer(Modifier.weight(1f))

                    Text(
                        viewModel.formatDate(state.post?.createdAt),
                        style = AppTextStyle.BodySmall.copy(color = DarkGray, fontWeight = FontWeight.Bold)
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
                        Box(
                            modifier = Modifier
                                .background(GroupSecondary, RoundedCornerShape(32.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .width(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                style = AppTextStyle.BodySmall
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (state.post?.studyId != null) {

                    StudyDetailCard(
                        uid = uid,
                        studyId = state.post!!.studyId,
                        study = state.study!!,
                        memberList = state.memberList
                    )

                }

                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Gray)
                )

                Spacer(Modifier.height(Dimens.Large))

                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    state.commentList.forEach { comment ->
                        val userName = state.userMap[comment.uid]?.nickname ?: ""
                        CommunityDetailComment(
                            uid,
                            commentList = comment,
                            userName = userName,
                            onReplyClick = {
                                viewModel.setReplyToCommentId(comment.commentId)
                            },
                            onDeleteClick = {
                                showCommentDialog = true
                                deleteTargetCommentId = comment.commentId
                                dialogInfo = "댓글"
                            }
                        )
                        if (state.commentReplyList.containsKey(comment.commentId)) {
                            state.commentReplyList[comment.commentId]?.forEach { reply ->
                                val replyUserName = state.userMap[reply.uid]?.nickname ?: ""
                                CommunityDetailCommentReply(
                                    uid,
                                    commentList = reply,
                                    userName = replyUserName,
                                    onDeleteClick = {
                                        showCommentDialog = true
                                        deleteTargetCommentId = reply.commentId
                                        dialogInfo = "답글"
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

            if(showCommentDialog) {

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