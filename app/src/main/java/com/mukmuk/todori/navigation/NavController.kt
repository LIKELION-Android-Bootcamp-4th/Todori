package com.mukmuk.todori.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/** 항상 home(A)만 바닥에 남기고 target 탭으로 이동 */
fun NavController.navigateKeepingHomeBase(
    targetRoute: String,
    todoRoute: String = BottomNavItem.Todo.route
) {
    this.navigate(targetRoute) {
        popUpTo(todoRoute) {
            inclusive = false
            saveState = false
        }
        // 만약 위 popUpTo(String) 이 버전 이슈로 안 되면 아래로 대체:
        // val homeId = this@navigateKeepingHomeBase.graph.findNode(homeRoute)?.id
        // if (homeId != null) { popUpTo(homeId) { inclusive = false; saveState = false } }

        launchSingleTop = true
        restoreState = false
    }
}

fun NavController.reselectTopLevel(startRouteOfTab: String) {
    this.popBackStack(startRouteOfTab, inclusive = false)
}
