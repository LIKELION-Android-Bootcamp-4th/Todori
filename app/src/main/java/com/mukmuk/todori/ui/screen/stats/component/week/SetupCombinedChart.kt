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

    val totalTodos = floatArrayOf(0f, 5f, 4f, 6f, 8f, 5f, 0f)
    val completedTodos = floatArrayOf(0f, 4f, 4f, 5f, 7f, 5f, 0f)

    val totalEntries = totalTodos.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }
    val completedEntries = completedTodos.mapIndexed { i, v -> BarEntry(i.toFloat(), v) }


    val totalSet = BarDataSet(totalEntries, "전체 TODO").apply {
        color = Weekly.copy(alpha = 0.3f).toArgb() // 파란 계열 (차분)
        setDrawValues(false)
    }


    val completedSet = BarDataSet(completedEntries, "완료 TODO").apply {
        color = Success.copy(alpha = 0.3f).toArgb()
        setDrawValues(false)
    }

    val barData = BarData(totalSet, completedSet).apply {
        barWidth = 0.35f
    }

    barChart.apply {
        data = barData
        description.isEnabled = false
        legend.isEnabled = true

        xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(days)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
            labelCount = days.size
            textSize = 14f
        }

        axisLeft.apply {
            axisMinimum = 0f
            textSize = 14f
        }

        axisRight.isEnabled = false

        groupBars(0f, 0.4f, 0.05f)
        animateXY(1000,1000)
    }
}

