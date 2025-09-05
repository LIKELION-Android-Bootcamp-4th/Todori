package com.mukmuk.todori.navigation

import androidx.navigation.NavController

fun NavController.navigateKeepingHomeBase(
    targetRoute: String,
    todoRoute: String = BottomNavItem.Todo.route
) {
    this.navigate(targetRoute) {
        popUpTo(todoRoute) {
            inclusive = false
            saveState = false
        }

        launchSingleTop = true
        restoreState = false
    }
}

fun NavController.reselectTopLevel(startRouteOfTab: String) {
    this.popBackStack(startRouteOfTab, inclusive = false)
}
