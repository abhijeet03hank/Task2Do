package com.hank.task2do.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hank.task2do.R
import com.hank.task2do.ui.MainActivity

class AlarmReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {

//        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val bundle = intent?.extras
        var title = bundle!!.getString(Constants.TASK_TITLE) // title of task
        var comment = bundle!!.getString(Constants.TASK_COMMENT) // comments of task

//        var strUser: String? = intent.getStringExtra(Constants.TASK_DATA)
        val int = Intent(context,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,0,int,0)

        val builder = NotificationCompat.Builder(context!!,Constants.NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.task_app_icon)
            .setContentTitle(title)
            .setContentText(comment)
            .setAutoCancel(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)


        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1,builder.build())


    }
}