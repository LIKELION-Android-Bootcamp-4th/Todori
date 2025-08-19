package com.mukmuk.todori.ui.screen.stats.component.day

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White


@Composable
fun ReflectionChip(
    label: String,
    filled: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = filled,
        onClick = onClick,
        label = {
            Text(label, style = AppTextStyle.BodySmall.copy(color = Color.Unspecified))
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = White,
            labelColor = Black,
            selectedContainerColor = UserPrimary,
            selectedLabelColor = Black,
        ),
        border = BorderStroke(1.dp, Black)
    )
}