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
import com.hank.task2do.util.Constants
import com.hank.task2do.util.LoadingDialog
import com.hank.task2do.util.ViewModelCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignUpViewModel(application: Application): AndroidViewModel(application) {

    lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null
    var myViewCallBack: ViewModelCallback? = null


    companion object {
        private val TAG = SignUpViewModel::class.qualifiedName
    }

    init {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.getReference(Constants.USER_PROFILE)
//        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }

    }


    fun registerUser(email: String, password:String, fullname: String){
        viewModelScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(
                email,
                password
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUser = auth.currentUser
                        val currentUserDb = databaseReference?.child(currentUser?.uid!!)
                        currentUserDb?.let {
                            it.child(Constants.USER_FULL_NAME)?.setValue(fullname)
                        }

                        Toast.makeText(
                            getApplication(),
                            "You have successfully created account!",
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
            myViewCallBack?.getResult(user as Object);
        }
    }
}