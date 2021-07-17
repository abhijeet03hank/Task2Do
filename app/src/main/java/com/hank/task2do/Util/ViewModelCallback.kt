package com.hank.task2do.util

import com.google.firebase.auth.FirebaseUser

interface ViewModelCallback {
     fun getResult( user: FirebaseUser)
}