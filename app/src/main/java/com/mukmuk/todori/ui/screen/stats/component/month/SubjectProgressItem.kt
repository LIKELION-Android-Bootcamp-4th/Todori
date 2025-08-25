package com.mukmuk.todori.ui.screen.stats.component.month

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.DarkGray
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.ProgressBackground

@Composable
fun SubjectProgressItem(subject: SubjectProgress) {
    Column {
        Text(
            text = subject.name,
            style = AppTextStyle.BodySmallMedium,
            color = Black
        )
        Spacer(modifier = Modifier.height(Dimens.Nano))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "완료율 ${subject.completionRate}%",
                style = AppTextStyle.BodyTinyNormal,
                color = DarkGray
            )
        }
        Spacer(modifier = Modifier.height(Dimens.Nano))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(ProgressBackground, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(subject.completionRate / 100f)
                    .background(subject.color, RoundedCornerShape(4.dp))
            )
        }
    }
}