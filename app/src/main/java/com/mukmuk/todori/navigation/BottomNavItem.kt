package com.mukmuk.todori.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Todo : BottomNavItem("todo", "TODO", Icons.Outlined.Description)
    object Stats : BottomNavItem("stats", "통계", Icons.Outlined.BarChart)
    object Home : BottomNavItem("home", "홈", Icons.Outlined.Alarm)
    object Study : BottomNavItem("study", "커뮤니티", Icons.Outlined.PeopleOutline)
    object MyPage : BottomNavItem("mypage", "마이페이지", Icons.Outlined.Person)

    companion object {
        val items = listOf(Todo, Stats, Home, Study, MyPage)
    }
}
