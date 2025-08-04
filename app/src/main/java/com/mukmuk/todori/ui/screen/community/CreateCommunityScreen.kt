package com.mukmuk.todori.ui.screen.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupPrimary
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommunityScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }

    var content by remember { mutableStateOf("") }

    var data = listOf("td", "asd")


    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(text = "게시글 작성")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
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
                value = name,
                onValueChange = {
                    name = it
                    isNameError = it.isBlank()
                },
                placeholder = {Text("스터디 명을 입력하세요", color = DarkGray, fontSize = 16.sp, fontFamily = NotoSans) },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 56.dp),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Black,
                    fontFamily = NotoSans
                ),
                singleLine = true,
                isError = isNameError,
                supportingText = {
                    if (isNameError) Text("스터디 명을 입력해주세요", fontFamily = NotoSans) else null
                }
            )



            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it

                },
                placeholder = {Text("스터디 설명을 작성 해주세요", color = DarkGray, fontSize = 16.sp, fontFamily = NotoSans) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(10.dp),
                minLines = 6
            )

            Spacer(Modifier.height(20.dp))

            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Gray)
            )

            Spacer(Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("내가 만든 스터디", color = Black, fontSize = 16.sp, fontFamily = NotoSans)
                Spacer(Modifier.weight(1f))
                Button (
                    onClick = {},
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GroupPrimary,
                        contentColor = Black
                    ),

                ) {
                    Text("불러오기", color = White, fontSize = 16.sp, fontFamily = NotoSans)
                }
            }

            Spacer(Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                data.forEach{tag ->
                    Box(
                        modifier = Modifier
                            .background(GroupPrimary, RoundedCornerShape(32.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .width(60.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = tag,
                            color = Black,
                            style = TextStyle(
                                fontFamily = NotoSans,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("작성")
            }
        }

    }

}