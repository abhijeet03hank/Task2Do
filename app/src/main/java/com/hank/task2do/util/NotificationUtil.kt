package com.hank.task2do.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hank.task2do.R
import com.hank.task2do.ui.MainActivity

class NotificationUtil
(base: Context) : ContextWrapper(base) {


    private var manager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    // Create channel for Android version 26+
    @TargetApi(Build.VERSION_CODES.O)
     fun createChannels() {
        val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)

        getManager().createNotificationChannel(channel)
    }



    // Get Manager
    fun getManager() : NotificationManager {
        if (manager == null) manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    fun getNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        return NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(Constants.NOTIFICATION_CHANNEL)
            .setContentText("Time to finish task")
            .setSmallIcon(R.drawable.task_app_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
    }
}