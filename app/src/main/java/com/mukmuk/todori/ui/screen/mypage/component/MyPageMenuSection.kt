package com.mukmuk.todori.ui.screen.mypage.component


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.R
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.MyPageCard

@Composable
fun MyPageMenuSection(
    onLevelClick: () -> Unit,
    onGoalClick: () -> Unit,
    onProfileEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MyPageCard, shape = RoundedCornerShape(Dimens.DefaultCornerRadius))
            .padding(horizontal = Dimens.Small, vertical = Dimens.XLarge),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MyPageMenuItem(
            imageRes = R.drawable.ic_fire1,
            label = "나의 레벨",
            onClick = onLevelClick
        )

        Divider(color = Gray, modifier = Modifier.height(64.dp).width(1.dp))

        MyPageMenuItem(
            imageRes = R.drawable.ic_flag1,
            label = "완료한 목표",
            onClick = onGoalClick
        )

        Divider(color = Gray, modifier = Modifier.height(64.dp).width(1.dp))

        MyPageMenuItem(
            imageRes = R.drawable.ic_profile1,
            label = "프로필 관리",
            onClick = onProfileEditClick
        )
    }
}

@Composable
fun MyPageMenuItem(
    imageRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(Dimens.Tiny))
        Text(text = label, style = AppTextStyle.MypageButtonText)
    }
}