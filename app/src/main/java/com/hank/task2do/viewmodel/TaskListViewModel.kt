package com.hank.task2do.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hank.task2do.Util.Constants
import com.hank.task2do.ui.LoginFragmentDirections
import com.hank.task2do.ui.TaskListFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application): AndroidViewModel(application)  {

    lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null

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
}