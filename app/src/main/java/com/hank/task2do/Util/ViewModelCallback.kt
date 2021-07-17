package com.hank.task2do.Util

import androidx.annotation.Nullable
import com.google.firebase.auth.FirebaseUser

interface ViewModelCallback {
     fun getResult( user: FirebaseUser)
}