package com.mukmuk.todori.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mukmuk.todori.PushChannels
import com.mukmuk.todori.R

object NotificationHelper {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun show(context: Context, title: String, body: String, deeplink: String?) {
        val link = deeplink?.takeIf { it.isNotBlank() } ?: "todori://todo"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val pi = PendingIntent.getActivity(
            context,
            link.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, PushChannels.DEFAULT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(if (title.isBlank()) context.getString(R.string.app_name) else title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
