package com.example.proyecto_notas.adapters

import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_notas.R
import com.example.proyecto_notas.entities.Task
import com.example.proyecto_notas.listeners.TaskListener
import org.w3c.dom.Text
import java.util.*

class TaskAdapter (private var tasks: List<Task>, private val taskListener: TaskListener) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var timer : Timer? = null
    private var tasksSource = tasks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.TaskViewHolder {
        return TaskAdapter.TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_task,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskAdapter.TaskViewHolder, position: Int) {
        holder.setTask(tasks[position])
        holder.layoutTask.setOnClickListener{
            taskListener.onTaskClicked(tasks[position], position)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView
        var textPriority: TextView
        var textLimitDateTime : TextView
        var layoutTask : LinearLayout

        init {
            textTitle = itemView.findViewById(R.id.taskTitle)
            textPriority = itemView.findViewById(R.id.taskPriority)
            textLimitDateTime = itemView.findViewById(R.id.taskLimitDateTime)
            layoutTask = itemView.findViewById(R.id.layoutTask)
        }

        fun setTask(task: Task) {
            textTitle.text = task.title
            textPriority.text = task.priority
            textLimitDateTime.text = task.limitDate + " - " + task.limitTime
        }
    }

    fun searchNotes(searchKeyword: String) {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                if (searchKeyword.trim().isEmpty()) {
                    tasks = tasksSource
                } else {
                    val temp = ArrayList<Task>()
                    for (task in tasksSource) {
                        if (task.title.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            || task.priority.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            temp.add(task)
                        }
                    }
                    tasks = temp
                }
                android.os.Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
            }
        }, 500)
    }

    fun cancelTimer(){
        if(timer != null){
            timer!!.cancel()
        }
    }
}