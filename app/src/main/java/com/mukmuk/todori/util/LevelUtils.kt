package com.mukmuk.todori.util

import com.mukmuk.todori.R

data class LevelInfo(
    val imageRes: Int,
    val name: String
)

fun getLevelInfo(level: Int): LevelInfo {
    return when(level) {
        1 -> LevelInfo(R.drawable.ic_level1, "도토리")
        2 -> LevelInfo(R.drawable.ic_level2, "루키")
        3 -> LevelInfo(R.drawable.ic_level3, "꾸준이")
        4 -> LevelInfo(R.drawable.ic_level4, "열정러")
        5 -> LevelInfo(R.drawable.ic_level5, "고수")
        else -> LevelInfo(R.drawable.ic_level6, "집중이")
    }
}