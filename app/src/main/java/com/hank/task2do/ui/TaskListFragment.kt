package com.hank.task2do.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hank.task2do.R
import com.hank.task2do.adapter.TaskListAdapter
import com.hank.task2do.databinding.FragmentTaskListBinding
import com.hank.task2do.model.Task
import com.hank.task2do.util.Constants
import com.hank.task2do.viewmodel.TaskListViewModel
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list.view.*
import java.util.*


class TaskListFragment : Fragment() {

    private lateinit var mTaskListViewmodel: TaskListViewModel
    var authUser : FirebaseUser? = null
    lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null
    private var fragmentFirstBinding: FragmentTaskListBinding? = null
    lateinit var tList : MutableList<Task>
    var adapter : TaskListAdapter? = null

    companion object {
        private val TAG = TaskListFragment::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_task_list, container, false)
         fragmentFirstBinding = FragmentTaskListBinding.bind(view)

        mTaskListViewmodel = ViewModelProvider(this).get(TaskListViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        authUser = auth.currentUser
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }
        try {
            arguments?.let {
                authUser = LoginFragmentArgs.fromBundle(it).authUser
            }
        }catch (exception :IllegalArgumentException){
        }

        //Adapter
        adapter  = TaskListAdapter()

        //RecyclerView
        val recyclerView = view.task_recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        tList = mutableListOf<Task>()

// For liveDate
//        mTaskListViewmodel.taskData?.observe(viewLifecycleOwner, Observer {
//                newTaskList -> adapter.setTaskData(newTaskList)
//        })

//Sample Values
//        val task = Task("qwe","asdf", Calendar.getInstance().time,Status.TODO)
//        val task2 = Task("qwew","asdfff", Calendar.getInstance().time,Status.TODO)
//        adapter.setTaskData(tList)
        checkForEmptyTaskList(tList)
        hideKeyboard()
        return view
    }

    fun initialise(){
//        currentUser?.let {
//            user_name_tv.text = currentUser.toString()
//        }
        val user = auth.currentUser
        val userRef = databaseReference?.child(user?.uid!!)
        try {
            userRef?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fragmentFirstBinding?.userNameTv?.text = "Hi " + snapshot.child(Constants.USER_FULL_NAME).value.toString()+"!"
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG,error.toString() )
                }
            })

            userRef?.child(Constants.TASK_DATA)?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(taskSnapshot in snapshot.children){
                            val task = taskSnapshot.getValue(Task::class.java)
                            task?.let {
                                tList.add(task)
                            }
                        }
                        adapter?.let {
                            it.setTaskData(tList)
                            checkForEmptyTaskList(tList)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG,error.toString())
                }
            })
        }catch (exception: Exception){
                Log.d(TAG,exception.toString() )
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.sign_out_button.setOnClickListener {
                authUser?.let {
                    mTaskListViewmodel.signOutUser()
                    Navigation.findNavController(requireView()).navigate(R.id.action_taskListFragment_to_loginFragment)
                }
            }
            it.add_task_button.setOnClickListener{
                Navigation.findNavController(requireView()).navigate(R.id.action_taskListFragment_to_addTaskFragment)
            }

            this.requireView().isFocusableInTouchMode = true
            this.requireView().requestFocus()
            this.requireView().setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK
            }

            initialise()
        }
    }

    fun checkForEmptyTaskList(taskList : MutableList<Task>){
        if(taskList.size > 0){
            fragmentFirstBinding?.let {
                it.taskRecyclerview.visibility = View.VISIBLE
                it.letsAddTaskRl.visibility =View.GONE
            }
        }else{
            fragmentFirstBinding?.let {
                it.taskRecyclerview.visibility = View.GONE
                it.letsAddTaskRl.visibility =View.VISIBLE
            }
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