package com.hank.task2do.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListViewModel(application: Application): AndroidViewModel(application)  {

    lateinit var auth: FirebaseAuth

    init {
        auth = FirebaseAuth.getInstance()
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