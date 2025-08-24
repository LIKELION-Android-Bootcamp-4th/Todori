package com.mukmuk.todori.ui.screen.stats.component.week

import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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

fun setupLineChart(
    lineChart: LineChart,
    plannedHours: FloatArray,
    actualHours: FloatArray
) {
    val days = arrayOf("일", "월", "화", "수", "목", "금", "토")
    val plannedEntries = plannedHours.mapIndexed { i, v -> Entry(i.toFloat(), v) }
    val actualEntries = actualHours.mapIndexed { i, v -> Entry(i.toFloat(), v) }

    val plannedSet = LineDataSet(plannedEntries, "목표 기준선").apply {
        color = Red.toArgb()
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
        enableDashedLine(10f, 5f, 0f)
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
            axisMaximum = (maxOf(plannedHours.maxOrNull() ?: 0f, actualHours.maxOrNull() ?: 0f) + 1f)
            granularity = 1f
            textSize = 14f
        }

        axisRight.isEnabled = false

        setVisibleYRangeMinimum(1f, YAxis.AxisDependency.LEFT)
        invalidate()

        animateX(1000, Easing.EaseInOutQuad)
    }
}

fun setupBarChart(
    barChart: BarChart,
    totalEntries: List<BarEntry>,
    completedEntries: List<BarEntry>
) {
    val days = arrayOf("일", "월", "화", "수", "목", "금", "토")

    val totalSet = BarDataSet(totalEntries, "전체 TODO").apply {
        color = Weekly.copy(alpha = 0.3f).toArgb()
        setDrawValues(false)
    }

    val completedSet = BarDataSet(completedEntries, "완료 TODO").apply {
        color = Success.copy(alpha = 0.5f).toArgb()
        setDrawValues(false)
    }

    val barSpace = 0.05f
    val groupSpace = 0.2f
    val barWidth = 0.35f

    val barData = BarData(totalSet, completedSet).apply {
        this.barWidth = barWidth
    }

    val groupCount = days.size


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

        groupBars(0f, groupSpace, barSpace)
        animateXY(1000, 1000)

        invalidate()
    }
}

