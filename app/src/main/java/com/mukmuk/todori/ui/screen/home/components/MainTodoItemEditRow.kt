package com.mukmuk.todori.ui.screen.home.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ButtonKakao
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainTodoItemEditableRow(
    title: String,
    isDone: Boolean,
    isRecordMode: Boolean,
    recordTime: String?,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, color = DarkGray, shape = RoundedCornerShape(DefaultCornerRadius))
            .background(
                color = if (isDone) Gray else {
                    if (isRecordMode) ButtonKakao
                    else White
                }, shape = RoundedCornerShape(DefaultCornerRadius)
            )
            .padding(Dimens.Small)
            .clickable { onCheckedChange(!isDone)
                       onItemClick()},
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isDone) UserPrimary else Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(Dimens.Small))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = AppTextStyle.BodySmall.copy(
                    color = if (isDone) DarkGray else Black,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                ),
                modifier = Modifier.padding(horizontal = Dimens.Small)
            )
        }

        Spacer(modifier = Modifier.width(Dimens.Small))

        Text(
            text = recordTime ?: "",
            style = AppTextStyle.BodySmall.copy(color = DarkGray)
        )
    }
}