package com.mukmuk.todori.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Todo : BottomNavItem("todo", "할 일", Icons.Default.Check)
    object Stats : BottomNavItem("stats", "통계", Icons.Default.Close)
    object Home : BottomNavItem("home", "홈", Icons.Default.Home)
    object Study : BottomNavItem("study", "스터디", Icons.Default.Edit)
    object MyPage : BottomNavItem("mypage", "마이페이지", Icons.Default.Person)

    companion object {
        val items = listOf(Todo, Stats, Home, Study, MyPage)
    }
}
