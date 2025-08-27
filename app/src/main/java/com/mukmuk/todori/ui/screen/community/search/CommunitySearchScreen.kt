package com.mukmuk.todori.ui.screen.community.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import com.mukmuk.todori.ui.screen.community.components.CommunityPost
import com.mukmuk.todori.ui.screen.community.components.CommunitySearchData
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonPrimary
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.Pretendard
import com.mukmuk.todori.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySearchScreen(
    onBack: () -> Unit,
    navController: NavHostController,
    viewModel: CommunityViewModel
) {
    var query by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    var showCommunitySearchData by remember { mutableStateOf(true) }

    var showCommunitySearch by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    val uid = Firebase.auth.currentUser?.uid.toString()

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCommunitySearch(uid)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = {
                                query = it
                                showCommunitySearchData = true
                                showCommunitySearch = false
                                            },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                cursorColor = Black,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = LightGray,
                                unfocusedContainerColor = LightGray,
                            ),
                            textStyle = TextStyle(
                                color = Black,
                                fontSize = 16.sp,
                                fontFamily = Pretendard,

                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGray, RoundedCornerShape(30.dp)),
                            shape = RoundedCornerShape(30.dp),
                            placeholder = { Text("검색어를 입력하세요", style = AppTextStyle.Body.copy(color = DarkGray)) },
                            singleLine = true,
                            maxLines = 1,
                            trailingIcon = {
                                IconButton(onClick = {
                                    if(query.isNotBlank() && query.length >= 2) {
                                        viewModel.loadSearchPosts(data = query)
                                        viewModel.createCommunitySearch(uid, query)
                                        viewModel.getCommunitySearch(uid)
                                        showCommunitySearchData = false
                                        showCommunitySearch = true
                                        focusManager.clearFocus()
                                    }
                                    else{
                                        showDialog = true
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search"
                                    )
                                }
                            }
                        )
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
            )



        },

        containerColor = White,


        
    ) { innerPadding ->

        if(state.isLoading){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ButtonPrimary)
            }
        }
        else if(state.communitySearchPostList.isEmpty() && !showCommunitySearchData){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    "검색 결과가 없습니다",
                    style = AppTextStyle.Body.copy(color = DarkGray, fontWeight = FontWeight.Bold)
                )
            }
        }
        else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp),
            ) {

                if (showCommunitySearchData && !showCommunitySearch) {
                    Text(
                        text = "최근 검색어",
                        style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(16.dp)
                    )

                    FlowRow(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                        state.communitySearchList.forEach { search ->
                            CommunitySearchData(
                                data = search,
                                onSetClick = {
                                    query = search
                                    viewModel.loadSearchPosts(data = query)
                                    viewModel.createCommunitySearch(uid, query)
                                    viewModel.getCommunitySearch(uid)
                                    showCommunitySearchData = false
                                    showCommunitySearch = true
                                    focusManager.clearFocus()
                                },
                                onDeleteClick = {
                                    viewModel.deleteCommunitySearch(uid, search)
                                    showCommunitySearchData = true
                                    showCommunitySearch = false
                                }
                            )
                            Spacer(modifier = Modifier.padding(Dimens.Tiny))
                        }
                    }
                } else if (!showCommunitySearchData && showCommunitySearch) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp)
                    ) {
                        items(state.communitySearchPostList) { post ->
                            CommunityPost(
                                post = post,
                                navController = navController,
                            )
                        }
                    }
                }


            }

            if (showDialog) {
                if (query.isBlank()) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("검색어를 입력해주세요", style = AppTextStyle.Body) },
                        containerColor = White,
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("확인")
                            }
                        }
                    )
                } else if (query.length < 2) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("검색어는 2자 이상 입력해주세요", style = AppTextStyle.Body) },
                        containerColor = White,
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("확인")
                            }
                        }
                    )
                }
            }
        }
    }
}