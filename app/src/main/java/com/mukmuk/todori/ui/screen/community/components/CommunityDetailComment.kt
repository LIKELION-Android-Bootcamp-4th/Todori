package com.mukmuk.todori.ui.screen.community.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mukmuk.todori.ui.screen.community.CommentUiModel
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailViewModel
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.util.getLevelInfo

@Composable
fun CommunityDetailComment(
    uid: String,
    comment: CommentUiModel,
    onDeleteClick: () -> Unit
) {
    val viewModel: CommunityDetailViewModel = hiltViewModel()

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.Tiny)
    ) {
        val levelInfo = getLevelInfo(comment.level)
        Image(
            painter = painterResource(id = levelInfo.imageRes),
            contentDescription = "레벨 이미지",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(1.dp, Gray, CircleShape)
        )

        Spacer(modifier = Modifier.width(Dimens.Tiny))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        comment.nickname,
                        style = AppTextStyle.BodyBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        viewModel.formatDate(comment.createdAt),
                        style = AppTextStyle.BodyTinyBold,
                        color = DarkGray
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (uid == comment.uid) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "삭제",
                            tint = Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                comment.content,
                style = AppTextStyle.Body
            )
        }
    }
}
