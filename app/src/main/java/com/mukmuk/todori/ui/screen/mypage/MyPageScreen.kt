package com.mukmuk.todori.ui.screen.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mukmuk.todori.data.model.User
import com.mukmuk.todori.ui.mypage.component.ProfileSection
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.TodoriTheme

@Composable
fun MyPageScreen() {
    // 테스트 유저
    val currentUser = User(
        uid = "123",
        nickname = "asd",
        intro = "asdasd",
        level = 3,
        rewardPoint = 1200
    )

    TodoriTheme {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .padding(Dimens.Medium)
            ) {
                ProfileSection(user = currentUser)
            }
        }
    }
}
