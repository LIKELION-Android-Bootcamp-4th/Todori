package com.mukmuk.todori.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.UserPrimary

@Composable
fun CommunitySearchData(data: String, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30))
            .background(UserPrimary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .width(60.dp)
            .clickable { onClick() },

        contentAlignment = Alignment.Center
    ){
        Text(
            text = data,
            style = AppTextStyle.Body,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

}