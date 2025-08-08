package com.mukmuk.todori.ui.screen.community.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import com.mukmuk.todori.ui.screen.community.components.CommunitySearchData
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.NotoSans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySearchScreen(
    onBack: () -> Unit,
) {
    var query by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    var setTd by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it
                                setTd = false },
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
                                fontFamily = NotoSans,

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
                                    viewModel.loadPosts(data = query)
                                    viewModel.createCommunitySearch("uid", query)
                                    viewModel.getCommunitySearch("uid")
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
                actions = {

                }
            )


        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){

            Text(
                text = "최근 검색어",
                style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(16.dp)
            )

            FlowRow (
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxWidth(),

            ) {
                state.communitySearchList.forEach { search ->
                    CommunitySearchData(
                        data = search,
                        onClick = {
                            query = search
                            viewModel.loadPosts(data = query)
                        }
                    )
                    Spacer(modifier = Modifier.padding(Dimens.Tiny))
                }
            }
        }
    }
}