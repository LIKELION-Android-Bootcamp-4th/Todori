package com.mukmuk.todori.ui.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.remote.user.User
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.getLevelInfo

@Composable
fun ProfileSection(
    user: User
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.Medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val levelInfo = getLevelInfo(user.level)

        Image(
            painter = painterResource(id = levelInfo.imageRes),
            contentDescription = "레벨 이미지",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .background(White)
                .border(width = 1.dp, shape = CircleShape, color = Gray)
        )

        Spacer(modifier = Modifier.width(Dimens.Medium))

        Column {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = user.nickname,
                    style = AppTextStyle.TitleMedium
                )
                Spacer(modifier = Modifier.width(Dimens.Medium))
                Text(
                    text = "Lv.${user.level} ${levelInfo.name}",
                    style = AppTextStyle.BodySmall.copy(color = DarkGray)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.Small))

            Text(
                text = user.intro ?: "한 줄 소개 부분입니다.",
                style = AppTextStyle.Body.copy(color = DarkGray),
            )
        }
    }
}
