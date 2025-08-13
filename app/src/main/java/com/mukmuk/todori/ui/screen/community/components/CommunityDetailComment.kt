package com.mukmuk.todori.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.Timestamp
import com.mukmuk.todori.R
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.data.remote.community.StudyPostComment
import com.mukmuk.todori.ui.screen.community.CommunityViewModel
import com.mukmuk.todori.ui.screen.community.detail.CommunityDetailViewModel
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.White

@Composable
fun CommunityDetailComment(
    uid: String,
    commentList: StudyPostComment,
    userName: String,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val viewModel: CommunityDetailViewModel = hiltViewModel()


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(White, RoundedCornerShape(10.dp))
            .border(1.dp, Gray, RoundedCornerShape(10.dp)),
    ){

        Column(
            modifier = Modifier.padding(Dimens.Tiny)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Gray, CircleShape),
                )

                Spacer(modifier = Modifier.width(Dimens.Tiny))

                Text(
                    userName,
                    style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { onReplyClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_comment),
                        modifier = Modifier.size(16.dp),
                        contentDescription = null,
                        tint = Black
                    )
                }

                if (uid == commentList.uid) {

                    IconButton(onClick = { onDeleteClick() }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }

                }
            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            Text(commentList.content, style = AppTextStyle.Body)

            Spacer(modifier = Modifier.height(Dimens.Tiny))

            Text(viewModel.formatDate(commentList.createdAt), style = AppTextStyle.BodySmall, color = DarkGray, fontWeight = FontWeight.Bold)


        }



    }

}

