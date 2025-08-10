package com.mukmuk.todori.ui.screen.stats.component

import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.mukmuk.todori.ui.theme.Dimens.DefaultCornerRadius
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.mukmuk.todori.ui.theme.AppTextStyle
import com.mukmuk.todori.ui.theme.Dimens
import com.mukmuk.todori.ui.theme.White
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mukmuk.todori.data.remote.dailyRecord.DailyRecord
import com.mukmuk.todori.ui.theme.UserPrimary
import java.time.LocalDate

@Composable
fun WeekGraph(record: List<DailyRecord>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = Dimens.Medium),
        shape = RoundedCornerShape(DefaultCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(Dimens.Medium)) {
            Text("주간 공부시간 통계", style = AppTextStyle.TitleSmall)
            Spacer(modifier = Modifier.height(Dimens.XLarge))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                factory = { context ->
                    BarChart(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        //제스처 비활성화
                        setTouchEnabled(false)
                        isDragEnabled = false
                        setScaleEnabled(false)
                        setPinchZoom(false)
                        isDoubleTapToZoomEnabled = false


                        axisLeft.apply {
                            isEnabled = false
                            axisMinimum = 0f
                        }
                        axisRight.isEnabled = false
                        legend.isEnabled = false
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            setDrawAxisLine(false)
                            granularity = 1f
                            textSize = 12f
                            valueFormatter = IndexAxisValueFormatter(listOf("일","월","화","수","목","금","토"))
                        }
                        description.isEnabled = false
                    }
                },
                update = { chart ->
                    val hoursByDay = FloatArray(7) { 0f }
                    record.forEach { r ->
                        runCatching {
                            val dayIndex = LocalDate.parse(r.date).dayOfWeek.value % 7
                            val hours = r.studyTimeMillis.toFloat() / 3600f
                            hoursByDay[dayIndex] += hours
                        }
                    }

                    val entries = (0..6).map { i -> BarEntry(i.toFloat(), hoursByDay[i]) }
                    val dataSet = BarDataSet(entries, "시간(h)").apply {
                        color = UserPrimary.toArgb()
                        setDrawValues(false)
                    }

                    chart.data = BarData(dataSet).apply { barWidth = 0.4f }
                    chart.invalidate()
                }
            )


        }
    }
}