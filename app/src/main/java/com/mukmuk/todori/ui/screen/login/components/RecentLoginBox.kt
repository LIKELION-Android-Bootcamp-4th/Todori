package com.mukmuk.todori.ui.screen.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun RecentLoginBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .offset(y = (-4).dp)
            .background(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = Dimens.Tiny, vertical = Dimens.Nano)
    ) {
        Text("최근 로그인", color = Color.White, style = AppTextStyle.BodyTinyMedium)
    }
}