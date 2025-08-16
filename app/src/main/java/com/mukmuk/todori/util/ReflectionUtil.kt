package com.mukmuk.todori.util

enum class ReflectionType { GOOD, IMPROVE, BLOCKER }

data class ReflectionState(
    val good: String = "",
    val improve: String = "",
    val blocker: String = ""
)

private const val P_GOOD = "잘한 점: "
private const val P_IMPROVE = "바꿀 점: "
private const val P_BLOCKER = "방해 요인: "


fun buildReflection(state: ReflectionState): String {
    val parts = buildList {
        if (state.good.isNotBlank()) add(P_GOOD + state.good.trim())
        if (state.improve.isNotBlank()) add(P_IMPROVE + state.improve.trim())
        if (state.blocker.isNotBlank()) add(P_BLOCKER + state.blocker.trim())
    }
    return parts.joinToString("\n")
}

fun parseReflection(text: String): ReflectionState {
    if (text.isBlank()) return ReflectionState()
    val parts = text.split("\n")
    fun strip(prefix: String, s: String) = s.removePrefix(prefix).trim()

    var good = ""
    var improve = ""
    var blocker = ""
    parts.forEach { raw ->
        when {
            raw.startsWith(P_GOOD) -> good = strip(P_GOOD, raw)
            raw.startsWith(P_IMPROVE) -> improve = strip(P_IMPROVE, raw)
            raw.startsWith(P_BLOCKER) -> blocker = strip(P_BLOCKER, raw)
        }
    }
    return ReflectionState(good, improve, blocker)
}
