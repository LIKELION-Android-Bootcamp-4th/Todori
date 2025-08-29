package com.mukmuk.todori.ui.screen.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mukmuk.todori.R
import com.mukmuk.todori.data.remote.community.StudyPost
import com.mukmuk.todori.ui.screen.community.StudyCategory
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupSecondary
import com.mukmuk.todori.ui.theme.White


@Composable
fun CommunityPost(
    post: StudyPost,
    navController: NavHostController,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimens.Large)
            .border(1.dp, Gray, RoundedCornerShape(10.dp))
            .height(maxOf(120.dp)),

        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        onClick = {
            navController.navigate("community/detail/${post.postId}")
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.Medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    post.title,
                    modifier = Modifier.weight(1f),
                    style = AppTextStyle.Body.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .border(1.dp, Gray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.PeopleAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Black
                    )
                    Spacer(Modifier.width(Dimens.Tiny))
                    Text(
                        post.memberCount.toString(),
                        style = AppTextStyle.BodySmall
                    )
                }
                Spacer(Modifier.width(Dimens.Tiny))
                Row(
                    modifier = Modifier
                        .border(1.dp, Gray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Black
                    )
                    Spacer(modifier = Modifier.width(Dimens.Tiny))
                    Text(
                        text = post.commentsCount.toString(),
                        style = AppTextStyle.BodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.Tiny))
            Text(
                post.content,
                style = AppTextStyle.Body.copy(color = DarkGray),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(Dimens.Tiny))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                post.tags.forEach { tag ->
                    val categoryColor = StudyCategory.entries.find { category ->
                        category.tags.contains(tag)
                    }?.color ?: GroupSecondary

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = categoryColor,
                    ) {
                        Text(
                            text = tag,
                            style = AppTextStyle.BodySmallMedium.copy(color = White),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimens.Tiny))
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

}