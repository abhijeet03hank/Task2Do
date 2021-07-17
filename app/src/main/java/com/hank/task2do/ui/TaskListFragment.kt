package com.hank.task2do.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.hank.task2do.R
import com.hank.task2do.viewmodel.LoginViewModel
import com.hank.task2do.viewmodel.TaskListViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list.view.*


class TaskListFragment : Fragment() {

    private lateinit var mTaskListViewmodel: TaskListViewModel
    var currentUser : FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        mTaskListViewmodel = ViewModelProvider(this).get(TaskListViewModel::class.java)
        arguments?.let {
            currentUser = LoginFragmentArgs.fromBundle(it).authUser
        }
        initialise()
        return view
    }

    fun initialise(){
//        currentUser?.let {
//            user_name_tv.text = currentUser.toString()
//        }
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.sign_out_button.setOnClickListener {
                currentUser?.let {
                    mTaskListViewmodel.signOutUser()
                }
            }
        }
    }

}