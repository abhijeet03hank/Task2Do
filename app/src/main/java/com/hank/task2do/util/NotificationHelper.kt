package com.hank.task2do.util

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hank.task2do.R
import com.hank.task2do.ui.MainActivity

class NotificationHelper {
    companion object {


        var NOTIFICATION_ID = "notification-id"
        var NOTIFICATION = "notification"

        @SuppressLint("ResourceAsColor")
        fun getNotification(
            context: Context,
            Title: String,
            content: String
        ): Notification? {
            val int = Intent(context, MainActivity::class.java)
            int!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, int, 0)

//           val intent = Intent(context,AlarmReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(context, 0,intent,0)
            val builder = NotificationCompat.Builder(context!!, Constants.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.task_app_icon)
                .setContentTitle(Title)
                .setContentText(content)
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setColor(R.color.purple_500)
            }
            return builder.build()
        }


        fun setAlarmManager(
            context: Context,
            pendingIntent: PendingIntent,
            futureInMillis: Long
        ): AlarmManager {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                futureInMillis!!,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            return alarmManager
        }

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name: CharSequence = Constants.NOTIFICATION_CHANNEL
                val description = "Channel for alarm manager"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(NOTIFICATION_ID, name, importance)
                channel.description = description
                val notificationManager = context?.getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(channel)
            }
        }

        fun createNotificationChannel(context: Context, name: String, description: String) {
            // 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 2
                val channelId = "${context.packageName}-$name"
                val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
                channel.description = description
                channel.setShowBadge(false)

                // 3
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun createNotification(context: Context,title:String,message:String){
            val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
            val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.drawable.task_app_icon) // 3
                setContentTitle(title) // 4
                setContentText(message) // 5
                priority = NotificationCompat.PRIORITY_DEFAULT // 7
                setAutoCancel(false) // 8
            }.apply {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                setContentIntent(pendingIntent)
            }

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(1001, notificationBuilder.build())
        }
    }

}