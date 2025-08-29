package com.mukmuk.todori.ui.screen.community


import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mukmuk.todori.ui.screen.community.components.CommunityListOption
import com.mukmuk.todori.ui.screen.community.components.CommunityPost
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonPrimary
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White


@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavHostController, viewModel: CommunityViewModel) {

    var selectedCategory by remember { mutableStateOf("전체") }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.selectedOption) {
        viewModel.loadPosts()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("커뮤니티", style = AppTextStyle.AppBar, textAlign = TextAlign.Center) },
                actions = {
                    IconButton(
                        onClick = { navController.navigate("community/search") }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "검색")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars),
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = White,
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
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "작성",
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
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
                        viewModel.setSelectedData(option)
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { expanded = true }) {
                        Text(
                            text = selectedCategory,
                            style = AppTextStyle.Body,
                            color = Black
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "드롭다운",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = White
                    ) {
                        DropdownMenuItem(
                            text = { Text("전체", style = AppTextStyle.BodySmallNormal) },
                            onClick = {
                                selectedCategory = "전체"
                                selectedTag = null
                                viewModel.setData("전체")
                                expanded = false
                            }
                        )

                        StudyCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName, style = AppTextStyle.BodySmallNormal) },
                                onClick = {
                                    selectedCategory = category.displayName
                                    selectedTag = null
                                    viewModel.setData(category.displayName)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                val tags = if (selectedCategory == "전체") {
                    StudyCategory.entries.flatMap { it.tags }
                } else {
                    StudyCategory.entries
                        .find { it.displayName == selectedCategory }?.tags ?: emptyList()
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 0.dp)
                ) {
                    items(tags) { tag ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Gray),
                            color = if (selectedTag == tag) Black else White,
                            modifier = Modifier
                                .clickable {
                                    if (selectedTag == tag) {
                                        selectedTag = null
                                        viewModel.setData(selectedCategory)
                                    } else {
                                        selectedTag = tag
                                        viewModel.setData(tag)
                                    }
                                }
                        ) {
                            Text(
                                text = tag,
                                color = if (selectedTag == tag) White else Black,
                                style = AppTextStyle.BodySmallNormal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
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
            } else if (state.postList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "게시글이 없습니다",
                        style = AppTextStyle.Body.copy(
                            color = DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
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