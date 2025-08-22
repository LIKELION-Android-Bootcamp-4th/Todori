package com.mukmuk.todori.ui.screen.stats.component.week

import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


// MPAndroidChart 설정 함수
fun setupCombinedChart(chart: CombinedChart) {
    // 샘플 데이터
    val days = arrayOf("월", "화", "수", "목", "금", "토", "일")
    val plannedHours = floatArrayOf(8f, 7f, 9f, 8f, 8f, 0f, 0f)
    val actualHours = floatArrayOf(7f, 9f, 6f, 9f, 8f, 0f, 0f)
    val totalTodos = floatArrayOf(5f, 4f, 6f, 8f, 5f, 0f, 0f)
    val completedTodos = floatArrayOf(4f, 4f, 5f, 7f, 5f, 0f, 0f)

    // Line Chart (학습 시간)
    val lineEntries1 = plannedHours.mapIndexed { index, value ->
        Entry(index.toFloat(), value)
    }
    val lineEntries2 = actualHours.mapIndexed { index, value ->
        Entry(index.toFloat(), value)
    }

    val lineDataSet1 = LineDataSet(lineEntries1, "계획 시간").apply {
        color = android.graphics.Color.parseColor("#FF6B6B")
        setCircleColor(android.graphics.Color.parseColor("#FF6B6B"))
        lineWidth = 3f
        circleRadius = 5f
        setDrawValues(false)
        setDrawFilled(false)
    }

    val lineDataSet2 = LineDataSet(lineEntries2, "실제 시간").apply {
        color = android.graphics.Color.parseColor("#4CAF50")
        setCircleColor(android.graphics.Color.parseColor("#4CAF50"))
        lineWidth = 3f
        circleRadius = 5f
        setDrawValues(false)
        setDrawFilled(true)
        fillColor = android.graphics.Color.parseColor("#4CAF50")
        fillAlpha = 50
    }

    val lineData = LineData(lineDataSet1, lineDataSet2)

    // Bar Chart (TODO)
    val barEntries1 = totalTodos.mapIndexed { index, value ->
        BarEntry(index.toFloat(), value)
    }
    val barEntries2 = completedTodos.mapIndexed { index, value ->
        BarEntry(index.toFloat(), value)
    }

    val barDataSet1 = BarDataSet(barEntries1, "전체 TODO").apply {
        color = android.graphics.Color.parseColor("#E0E0E0")
        setDrawValues(false)
    }

    val barDataSet2 = BarDataSet(barEntries2, "완료 TODO").apply {
        color = android.graphics.Color.parseColor("#4CAF50")
        setDrawValues(false)
    }

    val barData = BarData(barDataSet1, barDataSet2).apply {
        barWidth = 0.25f
    }

    // Combined Data
    val combinedData = CombinedData().apply {
        setData(lineData)
        setData(barData)
    }

    // Chart 설정
    chart.apply {
        data = combinedData
        description.isEnabled = false
        legend.isEnabled = false
        setDrawGridBackground(false)
        setBackgroundColor(android.graphics.Color.WHITE)

        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(days)
            granularity = 1f
            textColor = android.graphics.Color.GRAY
        }

        axisLeft.apply {
            setDrawGridLines(true)
            axisMinimum = 0f
            textColor = android.graphics.Color.GRAY
        }

        axisRight.isEnabled = false

        // 바 차트 그룹화
        barData.groupBars(-0.5f, 0.4f, 0.05f)

        invalidate()
    }
}