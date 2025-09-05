package com.mukmuk.todori.ui.screen.home.home_ocr

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.mukmuk.todori.ui.screen.home.components.TimerTextFieldInput
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Background
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeOcrScreen(
    navController: NavHostController,
    viewModel: HomeOcrViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        val auth = Firebase.auth
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                viewModel.loadProfile(user.uid)

            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "시간 추가",
                        style = AppTextStyle.AppBar
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Background),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back Button",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus(force = true)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.Medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardColors(
                        containerColor = Color(0xFFFFE0E2),
                        contentColor = Red,
                        disabledContentColor = Red,
                        disabledContainerColor = Red
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Medium, vertical = Dimens.Small)
                ) {
                    Text(
                        " ⚠\uFE0F 경고 : 추가된 시간은 되돌릴 수 없으니 신중하게 입력해주세요.",
                        color = Red,
                        style = AppTextStyle.BodyTinyBold,
                        modifier = Modifier.padding(Dimens.Medium)
                    )

                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.Medium),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.Medium),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.TouchApp,
                                    contentDescription = "선택 아이콘",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(Dimens.Small))
                                Text(
                                    text = "빠른 선택",
                                    style = AppTextStyle.BodySmall,
                                    color = DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimens.Medium))
                            Row {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.setTimeState(0, 30, 0)
                                        viewModel.onAddRecordTime()
                                        navController.navigateUp()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    border = BorderStroke(1.dp, Gray)
                                ) {
                                    Text("30분", color = Black)
                                }
                                Spacer(modifier = Modifier.width(Dimens.Small))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.setTimeState(1, 0, 0)
                                        viewModel.onAddRecordTime()
                                        navController.navigateUp()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    border = BorderStroke(1.dp, Gray)
                                ) {
                                    Text("1시간", color = Black)
                                }
                            }
                            Row {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.setTimeState(1, 30, 0)
                                        viewModel.onAddRecordTime()
                                        navController.navigateUp()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    border = BorderStroke(1.dp, Gray)
                                ) {
                                    Text("1시간 30분", color = Black)
                                }
                                Spacer(modifier = Modifier.width(Dimens.Small))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.setTimeState(2, 0, 0)
                                        viewModel.onAddRecordTime()
                                        navController.navigateUp()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    border = BorderStroke(1.dp, Gray)
                                ) {
                                    Text("2시간", color = Black)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Dimens.Medium))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Dimens.Medium),
                ) {
                    ElevatedCard(
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.Medium),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = "직접 입력 아이콘",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(Dimens.Small))
                                Text(
                                    text = "시간 직접 입력",
                                    style = AppTextStyle.BodySmall,
                                    color = DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimens.Medium))
                            TimerTextFieldInput(
                                initialHours = state.selfInputHours,
                                initialMinutes = state.selfInputMinutes,
                                initialSeconds = state.selfInputSeconds,
                                onTimeChanged = { hours, minutes, seconds ->
                                    viewModel.onSelfInputChanged(hours, minutes, seconds)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Button(
                onClick = {
                    viewModel.onAddRecordTime()
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Medium)
            ) {
                Text("추가하기")
            }
        }
    }
}
