package com.hank.task2do.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hank.task2do.adapter.TaskListAdapter
import com.hank.task2do.model.Task
import com.hank.task2do.util.Constants
import com.hank.task2do.util.ViewModelCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application): AndroidViewModel(application)  {

    var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null
    val taskData : LiveData<MutableList<Task>>? =null
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
                                "Task added Successfuly!",
                                Toast.LENGTH_SHORT
                            ).show()
                            updateUI(currentUser);
                        } else {
                            Log.d(TAG, it.exception.toString())
                            Toast.makeText(getApplication(), "Failed to add task! " +it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
    }

    fun updateUI( user: FirebaseUser?){
        user?.let {
            myViewCallBack?.getResult(user as Object);
        }
    }


}