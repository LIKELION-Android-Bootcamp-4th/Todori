package com.mukmuk.todori.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.UserPrimary

@Composable
fun CommunitySearchData(data: String, onSetClick: () -> Unit, onDeleteClick: () -> Unit) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(UserPrimary.copy(alpha = 0.6f))
            .widthIn(min = 72.dp, max = 160.dp)
            .clickable { onSetClick() },

        contentAlignment = Alignment.Center
    ) {
        Text(
            text = data,
            style = AppTextStyle.BodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(start = 10.dp, end = 24.dp, top = 6.dp, bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp)
                .clickable { onDeleteClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "삭제",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}