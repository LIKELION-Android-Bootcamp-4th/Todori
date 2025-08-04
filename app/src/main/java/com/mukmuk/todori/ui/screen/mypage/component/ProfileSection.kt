package com.mukmuk.todori.ui.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.data.model.User
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
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
        // 레벨 이미지

        val levelInfo = getLevelInfo(user.level)

        Image(
            painter = painterResource(id = levelInfo.imageRes),
            contentDescription = "레벨 이미지",
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .border(width = 1.dp, shape = CircleShape,color = Gray)
        )

        Spacer(modifier = Modifier.width(Dimens.Medium))

        // 닉네임, 레벨, 한 줄 소개
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
