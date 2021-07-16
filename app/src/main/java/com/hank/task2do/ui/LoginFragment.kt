package com.hank.task2do.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.hank.task2do.R
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.signUpText.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment) }
        return view
    }


}