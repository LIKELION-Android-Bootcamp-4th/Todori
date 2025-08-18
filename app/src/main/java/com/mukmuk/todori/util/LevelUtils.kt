package com.mukmuk.todori.util

import androidx.annotation.DrawableRes
import com.mukmuk.todori.R

data class LevelInfo(
    val imageRes: Int,
    val name: String,
    val maxPoint: Int
)

fun getLevelInfo(level: Int): LevelInfo {
    return when(level) {
        1 -> LevelInfo(R.drawable.ic_level1, "도토리", 100)
        2 -> LevelInfo(R.drawable.ic_level2, "루키", 200)
        3 -> LevelInfo(R.drawable.ic_level3, "꾸준이", 300)
        4 -> LevelInfo(R.drawable.ic_level4, "열정러", 600)
        5 -> LevelInfo(R.drawable.ic_level5, "고수", 1300)
        else -> LevelInfo(R.drawable.ic_level6, "집중이", 0)
    }
}

@DrawableRes
fun levelIconRes(level: Int): Int = when (level) {
    1 -> R.drawable.ic_level1
    2 -> R.drawable.ic_level2
    3 -> R.drawable.ic_level3
    4 -> R.drawable.ic_level4
    5 -> R.drawable.ic_level5
    else -> R.drawable.ic_level6
}