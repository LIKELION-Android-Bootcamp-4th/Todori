package com.mukmuk.todori.util

import com.mukmuk.todori.R

data class LevelInfo(
    val imageRes: Int,
    val name: String
)

fun getLevelInfo(level: Int): LevelInfo {
    return when(level) {
        3 -> LevelInfo(R.drawable.ic_level3, "꾸준이") //일단 귀여운 친구로 통일
        else -> LevelInfo(R.drawable.ic_level3, "임시")
    }
}