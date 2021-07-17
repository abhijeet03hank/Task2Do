package com.hank.task2do.ui

import android.os.Bundle
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hank.task2do.R
import com.hank.task2do.Util.Constants
import com.hank.task2do.viewmodel.LoginViewModel
import com.hank.task2do.viewmodel.TaskListViewModel
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list.view.*


class TaskListFragment : Fragment() {

    private lateinit var mTaskListViewmodel: TaskListViewModel
    var currentUser : FirebaseUser? = null
    lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null


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
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }
        initialise()
        return view
    }

    fun initialise(){
//        currentUser?.let {
//            user_name_tv.text = currentUser.toString()
//        }

        val user = auth.currentUser
        val userRef = databaseReference?.child(user?.uid!!)
        userRef?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user_name_tv.text = snapshot.child(Constants.USER_FULL_NAME).value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.sign_out_button.setOnClickListener {
                currentUser?.let {
                    mTaskListViewmodel.signOutUser()
                    Navigation.findNavController(requireView()).navigate(R.id.action_taskListFragment_to_loginFragment)
                }
            }
        }
    }

}