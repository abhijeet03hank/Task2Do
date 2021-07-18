package com.hank.task2do.viewmodel

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hank.task2do.adapter.TaskListAdapter
import com.hank.task2do.model.Task
import com.hank.task2do.ui.MainActivity
import com.hank.task2do.util.AlarmReceiver
import com.hank.task2do.util.Constants
import com.hank.task2do.util.NotificationHelper
import com.hank.task2do.util.ViewModelCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TaskListViewModel(application: Application): AndroidViewModel(application)  {

    var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null
    var myViewCallBack: ViewModelCallback? = null


    lateinit var adapter  : TaskListAdapter

    init {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }
    }

    companion object {
        private val TAG = TaskListViewModel::class.qualifiedName
    }

    fun signOutUser(){
        viewModelScope.launch(Dispatchers.IO) {
            auth.signOut()
        }
    }

    fun addNewTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = auth.currentUser
            val currentUserDb = databaseReference?.child(currentUser?.uid!!)
            currentUserDb?.let {
                it.child(Constants.TASK_DATA)?.child(task.title!!).setValue(task)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val currentUser = auth.currentUser
                            Toast.makeText(
                                getApplication(),
                                "Task added Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(task as Object);
                        } else {
                            Log.d(TAG, it.exception.toString())
                            Toast.makeText(getApplication(), "Failed to add task! " +it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
    }

    fun updateNewTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = auth.currentUser
            val currentUserDb = databaseReference?.child(currentUser?.uid!!)
            currentUserDb?.let {
                it.child(Constants.TASK_DATA)?.child(task.title!!).setValue(task)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val currentUser = auth.currentUser
                            Toast.makeText(
                                getApplication(),
                                "Task updated Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(task as Object);
                        } else {
                            Log.d(TAG, it.exception.toString())
                            Toast.makeText(getApplication(), "Failed to add task! " +it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
    }


    fun updateUI( user: Object?){
        user?.let {
            myViewCallBack?.getResult(user );
        }
    }

    fun setAlarm(context: Context,task: Task){
        var description = task.comment
        val strList = task.comment?.lines()
        if(strList!!.size > 0)
            description = strList[0]

        val currentTime = Calendar.getInstance()
        val delay = task.timer?.time!!.minus( currentTime.time.time)
        if (delay > 0)
            scheduleNotification(
                context,
            NotificationHelper.getNotification(getApplication(),task.title!!, description!!)!!,
            delay
        )
    }

    fun scheduleNotification(context: Context,notification: Notification,delay: Long){

//        val notificationIntent = Intent(getApplication(), MainActivity::class.java)

//        notificationIntent.putExtra(NotificationHelper.NOTIFICATION_ID, 1)
//        notificationIntent.putExtra(NotificationHelper.NOTIFICATION, notification)
//        val pendingIntent = PendingIntent.getBroadcast(
//            getApplication(),
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0,intent,0)

        val futureInMillis: Long = SystemClock.elapsedRealtime() + delay
        NotificationHelper.setAlarmManager(context,pendingIntent,futureInMillis)

    }

    fun createNotificationChannel(context: Context){
       NotificationHelper.createNotificationChannel(context)
    }
}