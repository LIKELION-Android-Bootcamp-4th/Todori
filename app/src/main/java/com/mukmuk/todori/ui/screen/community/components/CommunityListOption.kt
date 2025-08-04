package com.mukmuk.todori.ui.screen.community.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.White

@SuppressLint("SuspiciousIndentation")
@Composable
fun CommunityListOption(
    data: List<StudyPost> = emptyList()
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("인기순") }
    val options = listOf("인기순", "최신순")



        Row (
            modifier = Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "이런 주제의 스터디는 어때요?",
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.weight(1f))


            Box{
                OutlinedButton(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = White,
                        contentColor = Black
                    ),
                    modifier = Modifier.height(36.dp)
                        .width(82.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = selectedOption,
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontSize = 14.sp
                        )
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(White, RoundedCornerShape(10.dp))
                        .border(1.dp, Gray)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(
                                text = option,
                                style = TextStyle(
                                    fontFamily = NotoSans,
                                    fontSize = 14.sp
                                )
                            ) },
                            onClick = {
                                selectedOption = option
                                if (option == "인기순") {

                                }
                                expanded = false
                            },
                        )
                    }
                }
            }
        }




        




}