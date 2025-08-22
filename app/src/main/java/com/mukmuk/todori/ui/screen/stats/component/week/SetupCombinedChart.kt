package com.mukmuk.todori.ui.screen.stats.component.week

import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mukmuk.todori.R
import com.mukmuk.todori.ui.theme.Red
import com.mukmuk.todori.ui.theme.Success
import com.mukmuk.todori.ui.theme.Weekly

// MPAndroidChart 설정 함수
fun setupLineChart(lineChart: LineChart) {
    val days = arrayOf("일", "월", "화", "수", "목", "금", "토")
    val plannedHours = floatArrayOf(8f, 7f, 9f, 8f, 8f, 0f, 0f)
    val actualHours = floatArrayOf(7f, 9f, 6f, 9f, 8f, 0f, 0f)

    val plannedEntries = plannedHours.mapIndexed { i, v -> Entry(i.toFloat(), v) }
    val actualEntries = actualHours.mapIndexed { i, v -> Entry(i.toFloat(), v) }

    val plannedSet = LineDataSet(plannedEntries, "계획 시간").apply {
        color = Red.toArgb()
        setCircleColor(Red.toArgb())
        lineWidth = 2.5f
        circleRadius = 4f
        setDrawValues(false)

    }

    val actualSet = LineDataSet(actualEntries, "실제 시간").apply {
        color = Success.toArgb()
        setCircleColor(Success.toArgb())
        lineWidth = 2.5f
        circleRadius = 4f
        setDrawValues(false)
        setDrawFilled(true)
        fillDrawable = ContextCompat.getDrawable(lineChart.context, R.drawable.fill_actual)
    }


    lineChart.apply {
        data = LineData(plannedSet, actualSet)
        description.isEnabled = false
        legend.isEnabled = true

        xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            textSize = 14f
        }

        axisLeft.apply {
            axisMinimum = 0f
            textSize = 14f
        }

        axisRight.isEnabled = false

//        animateXY(500,500)
        animateX(1000, Easing.EaseInOutQuad)
    }
}

fun setupBarChart(barChart: BarChart) {
    val days = arrayOf("일", "월", "화", "수", "목", "금", "토")

    val totalTodos = floatArrayOf(3f, 5f, 4f, 6f, 8f, 5f, 9f)
    val completedTodos = floatArrayOf(3f, 4f, 4f, 5f, 7f, 5f, 8f)

    val totalEntries = totalTodos.mapIndexed { i, v ->
        val value = if (v == 0f) 0.0001f else v  // 최소 높이 보정
        BarEntry(i.toFloat(), value)
    }

    val completedEntries = completedTodos.mapIndexed { i, v ->
        val value = if (v == 0f) 0.0001f else v
        BarEntry(i.toFloat(), value)
    }

    val totalSet = BarDataSet(totalEntries, "전체 TODO").apply {
        color = Weekly.copy(alpha = 0.3f).toArgb()
        setDrawValues(false)
        highLightAlpha = 0  // 선택 효과 없앰
    }

    val completedSet = BarDataSet(completedEntries, "완료 TODO").apply {
        color = Success.copy(alpha = 0.5f).toArgb()
        setDrawValues(false)
        highLightAlpha = 0
    }

    val barSpace = 0.05f
    val groupSpace = 0.2f
    val barWidth = 0.35f

    val barData = BarData(totalSet, completedSet).apply {
        this.barWidth = barWidth
    }

    val groupCount = days.size

//
//    val barData = BarData(totalSet, completedSet).apply {
//        barWidth = 0.35f
//    }

    barChart.apply {
        data = barData
        description.isEnabled = false
        legend.isEnabled = true

        xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            isGranularityEnabled = true
            setCenterAxisLabels(true)
            textSize = 14f

            axisMinimum = 0f
            axisMaximum = 0f + barData.getGroupWidth(groupSpace, barSpace) * groupCount
        }

        axisLeft.apply {
            axisMinimum = 0f
            textSize = 14f
        }

        axisRight.isEnabled = false

        groupBars(0f, groupSpace, barSpace) // ✅ 막대 & 라벨 정렬
        animateXY(1000,1000)
    }
}

