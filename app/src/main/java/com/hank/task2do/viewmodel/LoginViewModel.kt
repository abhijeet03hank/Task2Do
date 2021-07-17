package com.hank.task2do.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hank.task2do.Util.Constants
import com.hank.task2do.Util.ViewModelCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class LoginViewModel(application: Application): AndroidViewModel(application) {

    var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    val database : FirebaseDatabase? = null
    var myViewCallBack: ViewModelCallback? = null

    companion object {
        private val TAG = LoginViewModel::class.qualifiedName
    }

    init {
        auth = FirebaseAuth.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }

    }

    fun loginUser(email: String, password:String){
        viewModelScope.launch(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(
                email,
                password
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUser = auth.currentUser
                        Toast.makeText(
                            getApplication(),
                            "Success! Logging in.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(currentUser);
                    } else {
                        Log.d(TAG, it.exception.toString())
                        Toast.makeText(getApplication(), "Sign Up Failed! " +it.exception.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    fun updateUI( user: FirebaseUser?){
        user?.let {
            myViewCallBack?.getResult(user);
        }
    }
}