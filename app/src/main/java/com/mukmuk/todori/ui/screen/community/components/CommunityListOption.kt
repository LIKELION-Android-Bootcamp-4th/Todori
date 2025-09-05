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
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White

@SuppressLint("SuspiciousIndentation")
@Composable
fun CommunityListOption(
    selectedOption: String,
    setData: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    val options = listOf("참가자 수", "날짜순")

        Row (
            modifier = Modifier.wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "이런 주제의 스터디는 어때요?",
                style = AppTextStyle.Body
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
                        style = AppTextStyle.BodySmall
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .background(White, RoundedCornerShape(10.dp))
                        .border(1.dp, Gray, RoundedCornerShape(10.dp))
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(
                                text = option,
                                style = AppTextStyle.BodySmall
                            ) },
                            onClick = {
                                setData(option)
                                expanded = false
                            },
                        )
                    }
                }
            }
        }




        




}