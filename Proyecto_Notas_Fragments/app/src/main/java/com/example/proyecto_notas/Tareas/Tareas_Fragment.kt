package com.example.proyecto_notas.Tareas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.proyecto_notas.Notas.Notas_Fragment
import com.example.proyecto_notas.NotesDataBase
import com.example.proyecto_notas.R
import com.example.proyecto_notas.activities.CreateTasksActivity
import com.example.proyecto_notas.adapters.TaskAdapter
import com.example.proyecto_notas.entities.Task
import com.example.proyecto_notas.listeners.TaskListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Tareas_Fragment : Fragment(), TaskListener {

    private var param1 : String? = null
    private var param2 : String? = null

    private lateinit var imageAddTaskMain : ImageView
    private lateinit var taskAdapter : TaskAdapter
    private lateinit var taskRecyclerView : RecyclerView
    private lateinit var taskList : MutableList<Task>

    private var taskClickedPosition = -1

    val REQUEST_CODE_ADD_TASK = 1
    val REQUEST_CODE_UPDATE_TASK = 2
    val REQUEST_CODE_SHOW_TASKS = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tareas_, container, false)
    }

    companion object{
        fun newInstance(param1: String, param2 : String) = Notas_Fragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()

        imageAddTaskMain = view.findViewById<ImageView>(R.id.imageAddTaskMain)
        imageAddTaskMain.setOnClickListener {
            startActivityForResult(
                Intent(context, CreateTasksActivity().javaClass),
                REQUEST_CODE_ADD_TASK
            )
        }

        taskRecyclerView = view.findViewById(R.id.recyclerview_tareas)
        taskRecyclerView.setLayoutManager(
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        )

        taskAdapter = TaskAdapter(taskList, this)
        taskRecyclerView.adapter = taskAdapter

        getTask(REQUEST_CODE_SHOW_TASKS, false)

        var inputSearch = view.findViewById<EditText>(R.id.inputSearchTask)
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                taskAdapter.cancelTimer()
            }

            override fun afterTextChanged(s: Editable) {
                if (taskList.size != 0) {
                    taskAdapter.searchNotes(s.toString())
                }
            }
        })
    }

    private fun dataInitialize(){
        taskList = ArrayList()
    }

    override fun onTaskClicked(task: Task?, position: Int) {
        taskClickedPosition = position
        var intent = Intent(requireContext(), CreateTasksActivity::class.java)
        intent.putExtra("IsViewOrUpdate", true)
        intent.putExtra("task", task.toString())
        startActivityForResult(intent, REQUEST_CODE_UPDATE_TASK)
    }

    private fun getTask(requestCode: Int, isTaskDeleted: Boolean){
        @SuppressLint("StaticFieldLeak")
        class GetTasksTask : AsyncTask<Void, Void, List<Task>>(){

            @Override
            override fun doInBackground(vararg params: Void): List<Task>? {
                return NotesDataBase.getDatabase(context)?.taskDao()?.getAllTask()
            }

            @Override
            override fun onPostExecute(result: List<Task>) {
                super.onPostExecute(result)
                if(requestCode == REQUEST_CODE_SHOW_TASKS){
                    taskList.addAll(result)
                    taskAdapter.notifyDataSetChanged()
                }
                else if(requestCode == REQUEST_CODE_ADD_TASK){
                    taskList.add(0, result.get(0))
                    taskAdapter.notifyItemInserted(0)
                    taskRecyclerView.smoothScrollToPosition(0)
                }
                else if(requestCode == REQUEST_CODE_UPDATE_TASK){
                    taskList.removeAt(taskClickedPosition)
                    if(isTaskDeleted){
                        taskAdapter.notifyItemRemoved(taskClickedPosition)
                    }
                    else{
                        taskList.add(taskClickedPosition, result.get(taskClickedPosition))
                        taskAdapter.notifyItemChanged(taskClickedPosition)
                    }
                }
            }
        }

        GetTasksTask().execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_ADD_TASK && resultCode == AppCompatActivity.RESULT_OK){
            getTask(REQUEST_CODE_ADD_TASK, false)
        }
        else if(requestCode == REQUEST_CODE_UPDATE_TASK && resultCode == AppCompatActivity.RESULT_OK){
            if(data != null){
                getTask(REQUEST_CODE_UPDATE_TASK, data.getBooleanExtra("isTaskDeleted", false))
            }
        }
    }
}