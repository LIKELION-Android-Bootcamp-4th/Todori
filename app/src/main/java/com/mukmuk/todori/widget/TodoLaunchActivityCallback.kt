package com.mukmuk.todori.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class TodoLaunchActivityCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val user = Firebase.auth.currentUser
        val intent = if (user != null) {
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("todori://app.todori.com/todo")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("todori://app.todori.com/login")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        context.startActivity(intent)
    }
}