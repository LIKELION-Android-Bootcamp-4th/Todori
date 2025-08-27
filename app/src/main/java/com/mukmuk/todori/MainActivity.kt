package com.mukmuk.todori

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mukmuk.todori.navigation.AppNavigation
import com.mukmuk.todori.navigation.BottomNavItem
import com.mukmuk.todori.navigation.navigateKeepingHomeBase
import com.mukmuk.todori.navigation.reselectTopLevel
import com.mukmuk.todori.ui.theme.Black
import com.mukmuk.todori.ui.theme.TodoriTheme
import com.mukmuk.todori.ui.theme.UserPrimary
import com.mukmuk.todori.ui.theme.White
import com.mukmuk.todori.util.installNotificationPermissionAndTokenSync
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "알림 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "알림 권한이 거부되었습니다. 일부 기능이 제한될 수 있습니다.", Toast.LENGTH_LONG).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        installNotificationPermissionAndTokenSync()
        setContent {
            TodoriTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavRoutes = BottomNavItem.items.map { it.route }
                val showBottomBar = bottomNavRoutes.contains(currentRoute)
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = White
                            ) {
                                BottomNavItem.items.forEach { item ->
                                    val selected = currentRoute?.startsWith(item.route) == true
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.label) },

                                        label = { Text(item.label) },
                                        selected = selected,
                                        onClick = {
                                            if (!selected) {
                                                navController.navigateKeepingHomeBase(item.route)
                                            } else {
                                                navController.reselectTopLevel(item.route)
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = UserPrimary,
                                            unselectedIconColor = Black,
                                            indicatorColor = Color.Transparent
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                 Log.d("MainActivity", "Notification permission already granted.")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}