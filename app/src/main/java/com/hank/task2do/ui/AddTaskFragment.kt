package com.hank.task2do.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hank.task2do.R
import com.hank.task2do.model.Status
import com.hank.task2do.model.Task
import com.hank.task2do.util.Constants
import com.hank.task2do.util.DateUtil
import com.hank.task2do.util.LoadingDialog
import com.hank.task2do.util.ViewModelCallback
import com.hank.task2do.viewmodel.TaskListViewModel
import kotlinx.android.synthetic.main.fragment_add_task.*
import kotlinx.android.synthetic.main.fragment_add_task.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*


class AddTaskFragment : Fragment() {

    private lateinit var mTaskListViewmodel: TaskListViewModel
    private var mTimer : Date? = null
    var currentUser : FirebaseUser? = null
    lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null
    private lateinit var loadingDialog : LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_add_task, container, false)
        mTaskListViewmodel = ViewModelProvider(this).get(TaskListViewModel::class.java)
        loadingDialog = LoadingDialog(requireActivity())

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }

        mTaskListViewmodel.myViewCallBack = object: ViewModelCallback{
            override fun getResult(objects: Object) {
                loadingDialog.dismissDialog()
                Navigation.findNavController(requireView()).navigate(R.id.action_addTaskFragment_to_taskListFragment)

            }

        }

        return  view
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.add_timer_button.setOnClickListener {
                pickDateTime()
            }
            it.save_task_button.setOnClickListener {
                addNewTask()
            }
            it.back_to_task_list_button.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(R.id.action_addTaskFragment_to_taskListFragment)
            }

        }
    }

    private fun addNewTask(){
        if(validateTask()){
            val title = add_task_title_ed.text.toString()
            val comments = add_task_comment_ed.text.toString()
            val task = Task(title, comments, mTimer,Status.TODO)
            mTaskListViewmodel.addNewTask(task)
            loadingDialog.startLoadingDialog()
//            Navigation.findNavController(requireView()).navigate(R.id.action_addTaskFragment_to_taskListFragment)

        }
    }

    fun validateTask(): Boolean {
        var isValidTask = false
        view?.let {
            if (TextUtils.isEmpty(add_task_title_ed.text.toString())) {
                add_task_title_ed.error = "Enter Title!"
                return isValidTask
            }
            if (TextUtils.isEmpty(add_task_comment_ed.text.toString())) {
                add_task_comment_ed.error = "Enter Comments!"
                return isValidTask
            }
            isValidTask = true
        }
        return isValidTask
    }

    private fun pickDateTime() {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                updateTaskDateTime(pickedDateTime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    fun updateTaskDateTime(taskDateTime : Calendar){
        mTimer = taskDateTime.time
         val dateStr = DateUtil.dateToString(taskDateTime,Constants.DATE_TIME_FORMAT_DD_MM_YYYY_HH_MM)
        add_timer_button.text = dateStr
    }

}