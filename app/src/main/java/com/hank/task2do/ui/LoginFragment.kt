package com.hank.task2do.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hank.task2do.R
import com.hank.task2do.util.LoadingDialog
import com.hank.task2do.util.ViewModelCallback
import com.hank.task2do.databinding.FragmentLoginBinding
import com.hank.task2do.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import java.util.*


class LoginFragment : Fragment(), ViewModelCallback {

    lateinit var auth: FirebaseAuth
    private lateinit var mLoginViewmodel: LoginViewModel
    private lateinit var dataBinding: FragmentLoginBinding
    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
//        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        view.signUpText.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment) }
        mLoginViewmodel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loadingDialog = LoadingDialog(requireActivity())
        auth = FirebaseAuth.getInstance()
        mLoginViewmodel.myViewCallBack = object: ViewModelCallback {
            override fun getResult(user: Object) {
                view?.let {
                    loadingDialog.dismissDialog()
                    val action =LoginFragmentDirections.actionLoginFragmentToTaskListFragment()
                    action.authUser = user as @Nullable FirebaseUser
                    Navigation.findNavController(requireView()).navigate(action)
                }
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(null!=currentUser){
            val action =LoginFragmentDirections.actionLoginFragmentToTaskListFragment()
            action.authUser = currentUser
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.loggin_button.setOnClickListener {
                hideKeyboard()
                if (validateUser()) {
                    loadingDialog.startLoadingDialog()
                    mLoginViewmodel.loginUser(
                        login_email_ed.text.toString(),
                        login_password_ed.text.toString())
                }
            }
        }

    }

    fun validateUser(): Boolean {
        var isValidUser = false
        view?.let {
            if (TextUtils.isEmpty(login_email_ed.text.toString())) {
                login_email_ed.error = "Enter Email!"
                return isValidUser
            }
            if (TextUtils.isEmpty(login_password_ed.text.toString())) {
                login_password_ed.error = "Enter Password!"
                return isValidUser
            }
            isValidUser = true
        }
        return isValidUser
    }

    override fun getResult(user: Object) {
        view?.let {
            loadingDialog.dismissDialog()

            val action =LoginFragmentDirections.actionLoginFragmentToTaskListFragment()
            action.authUser = user as @Nullable FirebaseUser
            Navigation.findNavController(requireView()).navigate(action)


        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}