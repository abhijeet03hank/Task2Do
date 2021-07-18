package com.hank.task2do.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.Nullable
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
import com.hank.task2do.util.*
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
    private lateinit var alarmManager: AlarmManager

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
                val currentTask = objects as @Nullable Task
                if(null!=currentTask.timer)
                    setAlarm(currentTask)
                Navigation.findNavController(requireView()).navigate(R.id.action_updateTaskFragment_to_taskListFragment)
            }
        }

        createNotificationChannel()

        return  view
    }

    override fun onResume() {
        super.onResume()
        fragmentUpdateTaskBinding?.let {
            it.updateTimerButton.setOnClickListener {
                hideKeyboard()
                pickDateTime(DateUtil.dateToCalendar(mTimer))

            }
            it.saveTaskButton.setOnClickListener {
                hideKeyboard()
                updateNewTask()
            }
            it.backToTaskListButton.setOnClickListener {
                hideKeyboard()
                Navigation.findNavController(requireView()).navigate(R.id.action_updateTaskFragment_to_taskListFragment)
            }

            it.updateStatusCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                selectedTask?.status =
                if (isChecked) {
                      Status.COMPLETED
                }else{
                     Status.TODO
                }
            }

//            it.taskItemDelete.setOnClickListener {
//                createNotificationChannel()
//                setAlarm()
//            }

            if(null!=selectedTask){
                populateTaskData(selectedTask!!)
            }

        }
    }

//    private fun setAlarm() {
//        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(requireActivity(),AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0,intent,0)
//        val timeInMillisecond = selectedTask?.timer?.time
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillisecond!!,AlarmManager.INTERVAL_DAY,pendingIntent)
//        Toast.makeText(requireContext(),"Scheduled a timer for the task", Toast.LENGTH_SHORT).show()
//    }

    private fun populateTaskData(task: Task){
        fragmentUpdateTaskBinding?.let {
            it.updateTaskCommentEd.setText(task.comment)
            it.updateTaskTitleEd.setText(task.title)
            it.updateTaskTitleEd.isEnabled = false
            it.updateStatusCheckbox.isChecked = task.status==Status.COMPLETED

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
            val task = Task(title, comments, mTimer,selectedTask?.status)
            mTaskListViewmodel.updateNewTask(task)
            loadingDialog.startLoadingDialog()

        }
    }

    fun validateTask(): Boolean {
        var isValidTask = false
        view?.let {
            if (TextUtils.isEmpty(update_task_title_ed.text.toString().trim())) {
                update_task_title_ed.error = "Enter Title!"
                return isValidTask
            }
            if (TextUtils.isEmpty(update_task_comment_ed.text.toString().trim())) {
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

    private fun setAlarm(task: Task) {

        var description = task.comment
        val strList = task.comment?.lines()
        if(strList!!.size > 0)
            description = strList[0]
        val currentTime = Calendar.getInstance()
        val delay = task.timer?.time!!.minus( currentTime.time.time)
        if (delay > 0) {
            val futureInMillis: Long = SystemClock.elapsedRealtime() + delay

            alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireActivity(), AlarmReceiver::class.java)
            intent.putExtra(Constants.TASK_TITLE, task.title)
            intent.putExtra(Constants.TASK_COMMENT, description)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                futureInMillis!!,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Toast.makeText(requireContext(), "Scheduled a timer for the task", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val description = "Channel for alarm manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL, Constants.NOTIFICATION_CHANNEL,importance)
            channel.description = description
            val notificationManager = context?.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
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