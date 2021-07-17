package com.hank.task2do.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.hank.task2do.R
import com.hank.task2do.adapter.TaskListAdapter
import com.hank.task2do.databinding.FragmentTaskListBinding
import com.hank.task2do.model.Status
import com.hank.task2do.model.Task
import com.hank.task2do.util.Constants
import com.hank.task2do.viewmodel.TaskListViewModel
import kotlinx.android.synthetic.main.fragment_task_list.*
import kotlinx.android.synthetic.main.fragment_task_list.view.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*


class TaskListFragment : Fragment() {

    private lateinit var mTaskListViewmodel: TaskListViewModel
    var currentUser : FirebaseUser? = null
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_task_list, container, false)
         fragmentFirstBinding = FragmentTaskListBinding.bind(view)


        mTaskListViewmodel = ViewModelProvider(this).get(TaskListViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }
        try {
            arguments?.let {
                currentUser = LoginFragmentArgs.fromBundle(it).authUser
            }
        }catch (exception :IllegalArgumentException){
        }

        //Adapter
        adapter  = TaskListAdapter()

        //RecyclerView
        val recyclerView = view.task_recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


//        mTaskListViewmodel.taskData?.observe(viewLifecycleOwner, Observer {
//                newTaskList -> adapter.setTaskData(newTaskList)
//        })

//        val task = Task("qwe","asdf", Calendar.getInstance().time,Status.TODO)
//        val task2 = Task("qwew","asdfff", Calendar.getInstance().time,Status.TODO)
         tList = mutableListOf<Task>()
//        adapter.setTaskData(tList)
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
                            it.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()
        view?.let {
            it.sign_out_button.setOnClickListener {
                currentUser?.let {
                    mTaskListViewmodel.signOutUser()
                    Navigation.findNavController(requireView()).navigate(R.id.action_taskListFragment_to_loginFragment)
                }
            }
            it.add_task_button.setOnClickListener{
                Navigation.findNavController(requireView()).navigate(R.id.action_taskListFragment_to_addTaskFragment)
            }

            initialise()
        }
    }

    private fun getTaskDataFromDb(){
        databaseReference = database?.getReference()
    }

}