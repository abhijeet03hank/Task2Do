package com.hank.task2do.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hank.task2do.R
import com.hank.task2do.model.Task
import com.hank.task2do.ui.TaskListFragmentDirections
import com.hank.task2do.util.Constants
import com.hank.task2do.util.DateUtil
import kotlinx.android.synthetic.main.task_list_item_layout.view.*


class TaskListAdapter: RecyclerView.Adapter<TaskListAdapter.MyViewHolder>() {

    private var taskList = emptyList<Task>()
    class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_list_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentTask = taskList[position]
        holder.itemView.task_item_title.text = currentTask.title
        currentTask.timer?.let {
            holder.itemView.task_item_date_time.text = DateUtil.dateToString(
                currentTask.timer,
                Constants.DATE_TIME_FORMAT_DD_MM_YYYY_HH_MM
            )
        }

        holder.itemView.task_item_view.setOnClickListener {
            val action = TaskListFragmentDirections.actionTaskListFragmentToUpdateTaskFragment(currentTask)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setTaskData(newTaskList: List<Task>){
        this.taskList = newTaskList
        notifyDataSetChanged()
    }
}