package com.mukmuk.todori.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukmuk.todori.R
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.White

@Composable
fun CommunitySearchData(data: String, onSetClick: () -> Unit, onDeleteClick: () -> Unit) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30))
            .background(White)
            .border(1.dp, DarkGray, RoundedCornerShape(30))
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .widthIn(max = 68 .dp)
            .clickable { onSetClick() },

        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = data,
                style = AppTextStyle.BodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 42.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { onDeleteClick() },
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "삭제",
                )
            }
        }
    }


}