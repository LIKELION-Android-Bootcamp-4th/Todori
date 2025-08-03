package com.mukmuk.todori.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.LightGray
import com.mukmuk.todori.ui.theme.UserPrimary

@Composable
fun CustomLinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = Dimens.Tiny,
    progressColor: Color = UserPrimary,
    trackColor: Color = LightGray,
    cornerRadius: Dp = Dimens.Nano
) {
    val cornerPx = with(LocalDensity.current) { cornerRadius.toPx() }

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(height)
    ) {
        val width = size.width
        val heightPx = size.height

        drawRoundRect(
            color = trackColor,
            size = Size(width, heightPx),
            cornerRadius = CornerRadius(cornerPx, cornerPx)
        )

        val progressWidth = width * progress.coerceIn(0f, 1f)
        if (progressWidth > 0f) {
            drawRoundRect(
                color = progressColor,
                size = Size(progressWidth, heightPx),
                cornerRadius = CornerRadius(cornerPx, cornerPx)
            )
        }
    }
}