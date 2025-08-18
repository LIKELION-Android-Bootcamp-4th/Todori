package com.mukmuk.todori.ui.screen.mypage.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens

@Composable
fun MyPageAccountSection(
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onStudyTargetsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Medium)
    ) {
        Divider(Modifier.padding(vertical = Dimens.Large))

        Text(
            text = "내 계정",
            style = AppTextStyle.BodySmall,
            color = DarkGray,
            modifier = Modifier.padding(vertical = Dimens.Small)
        )

        Text(
            text = "로그아웃",
            style = AppTextStyle.Body,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogoutClick() }
                .padding(vertical = Dimens.Small)
        )

        Text(
            text = "회원 탈퇴",
            style = AppTextStyle.Body,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDeleteAccountClick() }
                .padding(vertical = Dimens.Small)
        )

        Divider(Modifier.padding(vertical = Dimens.Large))

        Text(
            text = "설정",
            style = AppTextStyle.BodySmall,
            color = DarkGray,
            modifier = Modifier.padding(vertical = Dimens.Small)
        )

        Text(
            text = "공부 시간 설정",
            style = AppTextStyle.Body,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStudyTargetsClick() }
                .padding(vertical = Dimens.Small)
        )
    }
}
