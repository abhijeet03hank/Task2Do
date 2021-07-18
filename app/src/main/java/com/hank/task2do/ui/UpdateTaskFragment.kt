package com.hank.task2do.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hank.task2do.R
import com.hank.task2do.databinding.FragmentTaskListBinding
import com.hank.task2do.databinding.FragmentUpdateTaskBinding
import com.hank.task2do.model.Status
import com.hank.task2do.model.Task
import com.hank.task2do.util.Constants
import com.hank.task2do.util.DateUtil
import com.hank.task2do.util.LoadingDialog
import com.hank.task2do.util.ViewModelCallback
import com.hank.task2do.viewmodel.TaskListViewModel
import kotlinx.android.synthetic.main.fragment_update_task.*
import kotlinx.android.synthetic.main.fragment_update_task.view.*
import java.util.*


class UpdateTaskFragment : Fragment() {

    private lateinit var mTaskListViewmodel: TaskListViewModel
    private var mTimer : Date? = null
    lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database : FirebaseDatabase? = null
    private lateinit var loadingDialog : LoadingDialog
    private val args by navArgs<UpdateTaskFragmentArgs>()
    private var selectedTask : Task? = null
    private var fragmentUpdateTaskBinding: FragmentUpdateTaskBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_update_task, container, false)
        fragmentUpdateTaskBinding = FragmentUpdateTaskBinding.bind(view)

        mTaskListViewmodel = ViewModelProvider(this).get(TaskListViewModel::class.java)
        loadingDialog = LoadingDialog(requireActivity())

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.let { it.child(Constants.USER_PROFILE) }

        args?.let {
            selectedTask = it.currentTask
        }

        mTaskListViewmodel.myViewCallBack = object: ViewModelCallback {
            override fun getResult(objects: Object) {
                loadingDialog.dismissDialog()
                Navigation.findNavController(requireView()).navigate(R.id.action_updateTaskFragment_to_taskListFragment)
            }
        }

        return  view
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            it.update_timer_button.setOnClickListener {
                if(null!=mTimer)
                    pickDateTime(DateUtil.dateToCalendar(mTimer!!))

            }
            it.save_task_button.setOnClickListener {
                updateNewTask()
            }
            it.back_to_task_list_button.setOnClickListener {
                Navigation.findNavController(requireView()).navigate(R.id.action_updateTaskFragment_to_taskListFragment)
            }

            if(null!=selectedTask){
                populateTaskData(selectedTask!!)
            }

        }
    }

    private fun populateTaskData(task: Task){
        fragmentUpdateTaskBinding?.let {
            it.updateTaskCommentEd.setText(task.comment)
            it.updateTaskTitleEd.setText(task.title)
            it.updateTaskTitleEd.isEnabled = false
            if(null!=task.timer) {
                mTimer = task.timer
                updateTaskDateTime(task.timer)
            }
        }
    }

    private fun updateNewTask(){
        if(validateTask()){
            val title = update_task_title_ed.text.toString()
            val comments = update_task_comment_ed.text.toString()
            val task = Task(title, comments, mTimer, Status.TODO)
            mTaskListViewmodel.updateNewTask(task)
            loadingDialog.startLoadingDialog()

        }
    }

    fun validateTask(): Boolean {
        var isValidTask = false
        view?.let {
            if (TextUtils.isEmpty(update_task_title_ed.text.toString())) {
                update_task_title_ed.error = "Enter Title!"
                return isValidTask
            }
            if (TextUtils.isEmpty(update_task_comment_ed.text.toString())) {
                update_task_comment_ed.error = "Enter Comments!"
                return isValidTask
            }
            isValidTask = true
        }
        return isValidTask
    }

    private fun pickDateTime(calendarTime: Calendar?) {
        val currentDateTime =
            calendarTime ?: Calendar.getInstance()

        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, day ->
            TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                updateTaskDateTime(pickedDateTime.time)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }

    fun updateTaskDateTime(taskDateTime : Date){
        mTimer = taskDateTime
        val dateStr = DateUtil.dateToString(taskDateTime, Constants.DATE_TIME_FORMAT_DD_MM_YYYY_HH_MM)
        fragmentUpdateTaskBinding?.updateTimerButton?.setText(dateStr)
    }

}