package com.mukmuk.todori.ui.screen.community.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import com.mukmuk.todori.ui.screen.community.components.CommunityDetailItem
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
    onBack: () -> Unit,
    navController: NavController,
    viewModel: CommunityViewModel
) {
    val post = viewModel.selectedPost

    var scrollState = rememberScrollState()

    var expanded by remember { mutableStateOf(false) }

    var data = listOf("td", "asd")

    var commentContent by remember { mutableStateOf("") }

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
                                        if(item == "수정") {
                                            navController.navigate("community/create")
                                            viewModel.selectedPost = post
                                        }
                                        else if(item == "삭제") {
                                            viewModel.deletePost(post!!)
                                            onBack()
                                        }
                                    }
                                )
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
                        .border(1.dp, Gray, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("댓글을 작성해주세요", style = AppTextStyle.Body.copy(color = DarkGray)) },
                    singleLine = true,
                    maxLines = 1
                )


                Button(
                    onClick = {
                        commentContent = ""
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
                Text("사용자 이름", style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))

                Spacer(Modifier.weight(1f))

                Text("", style = AppTextStyle.BodySmall.copy(color = DarkGray))
            }

            Spacer(Modifier.height(8.dp))

            Text(post?.title ?: "", style = AppTextStyle.Title.copy(fontWeight = FontWeight.Bold))

            Spacer(Modifier.height(4.dp))





            Text(
                post?.content ?: "",
                style = AppTextStyle.Body
            )

            Spacer(Modifier.height(Dimens.Large))

            Row(

                verticalAlignment = Alignment.CenterVertically
            ) {
                data.forEach{ tag ->
                    Box(
                        modifier = Modifier
                            .background(GroupSecondary, RoundedCornerShape(32.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .width(60.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = tag,
                            style = AppTextStyle.BodySmall
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))


            CommunityDetailItem(
                name = "asd",
                description = "aoisjdoiasd",
                createdAt = null,
                joinedAt = null,
                memberCount = 10,
                activeDays = listOf("화", "수", "목")
            )

            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Gray)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ){


            }



        }

    }

}