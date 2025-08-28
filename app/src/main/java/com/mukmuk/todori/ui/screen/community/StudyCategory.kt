package com.mukmuk.todori.ui.screen.community

import androidx.compose.ui.graphics.Color

enum class StudyCategory(
    val displayName: String,
    val tags: List<String>,
    val color: Color
) {
    IT_DEV(
        "IT·개발",
        listOf("프로그래밍", "데이터", "AI", "웹/앱", "소프트웨어", "클라우드"),
        Color(0xFFF29985)
    ),
    CERTIFICATION(
        "자격증·시험",
        listOf("국가자격증", "어학시험", "IT자격증", "전문직", "코딩테스트"),
        Color(0xFFD97904)
    ),
    ACADEMICS(
        "학문·교양",
        listOf("수학/통계", "경제/경영", "자연과학", "인문/사회", "논술"),
        Color(0xFFe9a72c)
    ),
    LANGUAGE(
        "언어·소통",
        listOf("외국어", "회화", "작문", "발표/토론", "글쓰기"),
        Color(0xFFF4A261)
    ),
    SELF_GROWTH(
        "자기계발·취미",
        listOf("취업/이직", "재테크/투자", "운동/건강", "예술/디자인", "독서"),
        Color(0xFFE76F51)
    )
}