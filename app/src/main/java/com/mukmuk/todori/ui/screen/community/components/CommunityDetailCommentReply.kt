package com.mukmuk.todori.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailViewModel
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.White

@Composable
fun CommunityDetailCommentReply(
    userName: String,
    comment: String,
    createdAt: Timestamp?,
    viewModel: CommunityDetailViewModel,
) {

    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(Dimens.Tiny))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(LightGray, RoundedCornerShape(10.dp))
                .border(1.dp, Gray, RoundedCornerShape(10.dp)),
        ){

            Column(
                modifier = Modifier.padding(Dimens.Tiny)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Gray, CircleShape),
                    )

                    Spacer(modifier = Modifier.width(Dimens.Tiny))

                    Text(userName, style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold))

                    Spacer(modifier = Modifier.weight(1f))

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
                        viewModel.td.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item, style = AppTextStyle.BodySmall) },
                                onClick = {  expanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.Tiny))

                Text(comment, style = AppTextStyle.Body)

                Spacer(modifier = Modifier.height(Dimens.Tiny))

                Text(createdAt?.toDate().toString(), style = AppTextStyle.BodySmall)


            }

        }
    }



}