package com.mukmuk.todori.util


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.mukmuk.todori.data.service.MyFirebaseMessagingService

fun ComponentActivity.installNotificationPermissionAndTokenSync() {
    val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            MyFirebaseMessagingService.ensureTokenSync()
        }
    }

    if (Build.VERSION.SDK_INT >= 33) {
        val perm = Manifest.permission.POST_NOTIFICATIONS
        val granted = ContextCompat.checkSelfPermission(this, perm) ==
                PackageManager.PERMISSION_GRANTED
        if (!granted) {
            launcher.launch(perm)
        } else {
            MyFirebaseMessagingService.ensureTokenSync()
        }
    } else {
        MyFirebaseMessagingService.ensureTokenSync()
    }
}
