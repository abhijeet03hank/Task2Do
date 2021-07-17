package com.hank.task2do.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hank.task2do.R
import com.hank.task2do.util.Constants
import com.hank.task2do.util.ViewModelCallback
import com.hank.task2do.databinding.FragmentSignUpBinding
import com.hank.task2do.util.LoadingDialog
import com.hank.task2do.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import java.util.*


class SignUpFragment : Fragment(),ViewModelCallback {
    lateinit var auth: FirebaseAuth
    private lateinit var mSignUpViewModel: SignUpViewModel
    private lateinit var dataBinding: FragmentSignUpBinding
    var databaseReference: DatabaseReference? = null
    val database: FirebaseDatabase? = null
    private lateinit var loadingDialog : LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        loadingDialog = LoadingDialog(requireActivity())

        view.signup_login_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        mSignUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        mSignUpViewModel.myViewCallBack = object: ViewModelCallback {
            override fun getResult(user: Object) {
                view?.let {
                    loadingDialog.dismissDialog()
                    Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_loginFragment)
                }

            }
        }

//        return dataBinding.root
        return view

    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.signup_next_button.setOnClickListener {
                if (validateUser()) {
                    mSignUpViewModel.registerUser(
                        signup_email_ed.text.toString(),
                        signup_password_ed.text.toString(),
                        signup_full_name_ed.text.toString()
                    )
                }
            }
        }
    }


    fun validateUser(): Boolean {
        var isValidUser = false
        view?.let {

            if (TextUtils.isEmpty(signup_full_name_ed.text.toString())) {
//            dataBinding.signupFullname.setError( getString(R.string.enter_full_name))
//            dataBinding.signupFullNameEd.setError( "Enter full name",null)
                signup_full_name_ed.error = "Enter full name!"
//                Toast.makeText(requireContext(), "Enter full name!", Toast.LENGTH_SHORT).show()
                return isValidUser
            }
//            if (TextUtils.isEmpty(signup_username_ed.text.toString())) {
//                signup_full_name_ed.error = "Enter Username!"
//                return isValidUser
//            }
            if (TextUtils.isEmpty(signup_email_ed.text.toString())) {
                signup_email_ed.error = "Enter Email!"
                return isValidUser
            }
            if (TextUtils.isEmpty(signup_password_ed.text.toString())) {
                signup_password_ed.error = "Enter Password!"
                return isValidUser
            }
            isValidUser = true
        }
        return isValidUser
    }




    override fun getResult(obj: Object) {
        view?.let {
            loadingDialog.dismissDialog()
            Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }


}