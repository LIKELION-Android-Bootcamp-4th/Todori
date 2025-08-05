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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.mukmuk.todori.R
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.GroupSecondary
import com.mukmuk.todori.ui.theme.NotoSans
import com.mukmuk.todori.ui.theme.White


@Composable
fun CommunityListItem(
    title: String,
    description: String,
    tags: List<String>,
    comments: Int,
    members: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .border(1.dp, Gray, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        onClick = {
            navController.navigate("community/detail")
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = Black,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        fontFamily = NotoSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .border(1.dp, Gray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Black
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = "$members",
                        color = Black,
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = DarkGray,
                style = TextStyle(
                    fontFamily = NotoSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(GroupSecondary, RoundedCornerShape(32.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .width(60.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = tag,
                            color = Black,
                            style = TextStyle(
                                fontFamily = NotoSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_comment),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),

                        tint = Black
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "$comments",
                        color = Black,
                        style = TextStyle(
                            fontFamily = NotoSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }

}