package com.mukmuk.todori.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import com.mukmuk.todori.ui.theme.GoalPrimary
import com.mukmuk.todori.ui.theme.Gray
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.calculateDDayValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoItemEditableRow(
    title: String,
    isDone: Boolean,
    dueDate: String? = null,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, color = DarkGray, shape = RoundedCornerShape(DefaultCornerRadius))
            .background(color = if (isDone) Gray else White, shape = RoundedCornerShape(DefaultCornerRadius))
            .padding(Dimens.Small)
            .clickable { onCheckedChange(!isDone) },
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            imageVector = if (isDone) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isDone) UserPrimary else Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(Dimens.Small))

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
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
            dueDate?.let {
                val dDay = remember(dueDate) { calculateDDayValue(it) }
                dDay?.let {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (dDay < 0) Red else if (dDay < 10) Red else GoalPrimary)
                            .padding(vertical = Dimens.Nano, horizontal = Dimens.Tiny)
                    ) {
                        Text(
                            text = if (dDay < 0) "종료" else "D-$dDay",
                            style = AppTextStyle.BodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(Dimens.Small))

        trailingContent()
    }
}