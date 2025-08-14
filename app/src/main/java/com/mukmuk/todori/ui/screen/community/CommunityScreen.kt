package com.mukmuk.todori.ui.screen.community


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mukmuk.todori.ui.screen.community.components.CommunityPost
import com.mukmuk.todori.ui.screen.community.components.CommunityListOption
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonPrimary
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White


@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController, viewModel: CommunityViewModel) {

    var selectedCategory by remember { mutableStateOf("전체") }


    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPosts()
    }

    val categories = remember(state.allPostList){
        buildList{
            add("전체")
            addAll(
                state.allPostList
                    .flatMap { it.tags }
                    .filter { it.isNotBlank() }
                    .groupingBy { it }
                    .eachCount()
                    .toList()
                    .sortedByDescending { it.second }
                    .map { it.first }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("커뮤니티", style = AppTextStyle.AppBar) },
                actions = {
                    IconButton(
                        onClick = {

                            navController.navigate("community/search")
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "검색")
                    }
                },
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("community/create")
                },
                shape = CircleShape,
                containerColor = ButtonPrimary,
                modifier = Modifier.size(60.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Outlined.Edit,
                    contentDescription = "작성",
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ){innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White)
        ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommunityListOption(
                        selectedOption = state.selectedOption,
                        setData = { option ->
                            selectedCategory = "전체"
                            viewModel.setData(selectedCategory)
                            if(option == "참가자 수"){
                                viewModel.loadPosts("참가자 수")
                            }
                            else if(option == "날짜순"){
                                viewModel.loadPosts("날짜순")
                            }
                        }
                    )
                }

            

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        Spacer(modifier = Modifier.width(Dimens.Tiny))
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Gray,
                                    shape = RoundedCornerShape(30)
                                )
                                .background(
                                    if (selectedCategory == category) Black else White,
                                    shape = RoundedCornerShape(30)
                                )
                                .clickable {
                                    selectedCategory = category
                                    viewModel.setData(selectedCategory)
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 14.sp,
                                color = if (selectedCategory == category) White else Black
                            )
                        }
                    }
                }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ButtonPrimary)
                }
            }
            else if (state.postList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("게시글이 없습니다", style = AppTextStyle.Body.copy(color = DarkGray, fontWeight = FontWeight.Bold))
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp)
                    ) {
                        items(state.postList) { post ->
                            CommunityPost(
                                post = post,
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }


    }
}