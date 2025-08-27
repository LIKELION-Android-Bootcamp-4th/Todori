package com.mukmuk.todori.ui.screen.stats.component.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.ReportPrimary

@Composable
fun HourlyHighlightCircle(
    hourRange: IntRange,
    centerText: String,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 20f,
    highlightColor: Color = ReportPrimary,
    trackColor: Color = Color.LightGray.copy(alpha = 0.3f)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val hours = hourRange.last - hourRange.first + 1
            val sweepPerHour = 360f / 24f
            val startAngle = -90f + (hourRange.first * sweepPerHour)
            val sweepAngle = sweepPerHour * hours

            drawArc(
                color = highlightColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = centerText,
            style = AppTextStyle.TitleMedium,
            color = Black
        )
    }
}
