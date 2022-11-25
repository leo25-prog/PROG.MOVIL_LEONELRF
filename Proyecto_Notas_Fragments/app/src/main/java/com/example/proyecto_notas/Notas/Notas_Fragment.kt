package com.example.proyecto_notas.Notas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.proyecto_notas.NotesDataBase
import com.example.proyecto_notas.R
import com.example.proyecto_notas.activities.CreateNoteActivity
import com.example.proyecto_notas.adapters.NotesAdapter
import com.example.proyecto_notas.entities.Note
import com.example.proyecto_notas.listeners.NoteListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Notas_Fragment : Fragment(), NoteListener {

    private var param1 : String? = null
    private var param2 : String? = null

    private lateinit var imageAddNoteMain : ImageView
    private lateinit var notesAdapter : NotesAdapter
    private lateinit var notesRecyclerView : RecyclerView
    private lateinit var noteList : MutableList<Note>

    private var noteClickedPosition = -1

    val REQUEST_CODE_ADD_NOTE = 1
    val REQUEST_CODE_UPDATE_NOTE = 2
    val REQUEST_CODE_SHOW_NOTES = 3

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
        return inflater.inflate(R.layout.fragment_notas_, container, false)
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

        imageAddNoteMain = view.findViewById<ImageView>(R.id.imageAddNoteMain)
        imageAddNoteMain.setOnClickListener{
            startActivityForResult(
                Intent(context, CreateNoteActivity().javaClass),
                REQUEST_CODE_ADD_NOTE
            )
        }

        notesRecyclerView = view.findViewById(R.id.recyclerview_notas)
        notesRecyclerView.setLayoutManager(
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        )

        notesAdapter = NotesAdapter(noteList, this)
        notesRecyclerView.adapter = notesAdapter

        getNotes(REQUEST_CODE_SHOW_NOTES, false)

        var inputSearch = view.findViewById<EditText>(R.id.inputSearch)
        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                notesAdapter.cancelTimer()
            }

            override fun afterTextChanged(s: Editable) {
                if (noteList.size != 0) {
                    notesAdapter.searchNotes(s.toString())
                }
            }
        })
    }

    private fun dataInitialize(){
        noteList = ArrayList()
    }

    override fun onNoteClicked(note: Note?, position: Int) {
        noteClickedPosition = position
        var intent = Intent(requireContext(), CreateNoteActivity::class.java)
        intent.putExtra("IsViewOrUpdate", true)
        intent.putExtra("note", note.toString())
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE)
    }

    private fun getNotes(requestCode: Int, isNoteDeleted: Boolean){
        @SuppressLint("StaticFieldLeak")
        class GetnotesTask : AsyncTask<Void, Void, List<Note>>(){

            @Override
            override fun doInBackground(vararg params: Void): List<Note>? {
                return NotesDataBase.getDatabase(context)?.noteDao()?.getAllNotes()
            }

            @Override
            override fun onPostExecute(result: List<Note>) {
                super.onPostExecute(result)
                //Log.d("My_notes", result.toString())
                if(requestCode == REQUEST_CODE_SHOW_NOTES){
                    noteList.addAll(result)
                    notesAdapter.notifyDataSetChanged()
                }
                else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0, result.get(0))
                    notesAdapter.notifyItemInserted(0)
                    notesRecyclerView.smoothScrollToPosition(0)
                }
                else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                    noteList.removeAt(noteClickedPosition)

                    if(isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition)
                    }
                    else{
                        noteList.add(noteClickedPosition, result.get(noteClickedPosition))
                        notesAdapter.notifyItemChanged(noteClickedPosition)
                    }

                }
            }
        }

        GetnotesTask().execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == AppCompatActivity.RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE, false)
        }
        else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == AppCompatActivity.RESULT_OK){
            if(data != null){
                Log.d("My_notes", "AVER  ")
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false))
            }
        }
    }
}