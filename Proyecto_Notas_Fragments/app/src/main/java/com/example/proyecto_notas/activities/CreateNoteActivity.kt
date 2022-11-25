package com.example.proyecto_notas.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.proyecto_notas.NotesDataBase
import com.example.proyecto_notas.R
import com.example.proyecto_notas.entities.Note
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteSubtitle: EditText
    private lateinit var inputNoteText: EditText
    private lateinit var textDateTime: TextView
    private lateinit var imageBack: ImageView
    private lateinit var selectdNoteColor: String
    private lateinit var viewSubtitleIndicator: View
    private lateinit var imageNote: ImageView
    private lateinit var selectedImagePath: String
    private lateinit var textWebURL: TextView
    private lateinit var layoutWebURL: LinearLayout

    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private val REQUEST_CODE_SELECT_IMAGE = 2

    private var dialogAddURL: AlertDialog? = null
    private var dialogDeleteNote: AlertDialog? = null

    private var alreadyAvailableNote : Note? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        imageBack = findViewById(R.id.imageBack)
        imageBack.setOnClickListener{
            onBackPressed()
        }

        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle)
        inputNoteText = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.textDateTime)
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator)
        imageNote = findViewById(R.id.imageNote)
        textWebURL = findViewById(R.id.textWebURL)
        layoutWebURL = findViewById(R.id.layoutWebURL)

        textDateTime.setText(
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
        )

        lateinit var imageSave : ImageView
        imageSave = findViewById(R.id.imageSave)
        imageSave.setOnClickListener{
            saveNote()
        }

        selectdNoteColor = "#333333"
        selectedImagePath = ""

        if (intent.getBooleanExtra("IsViewOrUpdate", false)) {
            var note = Note(
                uid = 0,
                title = "",
                subtitle = "",
                noteText = "",
                dateTime = "",
                color = "",
                imagePath = "",
                webLink = ""
            )

            var prueba = intent.getSerializableExtra("note").toString()
            var dato = ""
            var pos = 0
            var con = 0
            var cuenta = false
            while(pos < prueba.length){
                if(prueba[pos] == '=') cuenta = true
                else if(prueba[pos] == ','){
                    if(con != 2) {

                        if (con == 0) note.uid = dato.toInt()
                        else if (con == 1) note.title = dato
                        else if (con == 3) note.dateTime = dato
                        else if (con == 4) note.subtitle = dato
                        else if (con == 5) note.noteText = dato
                        else if (con == 6) note.imagePath = dato
                        else if (con == 7) note.color = dato
                        else if (con == 8) note.webLink = dato

                        cuenta = false
                        dato = ""
                    }
                    else dato += ","
                    con ++
                }
                else if(cuenta){
                    dato += prueba[pos]
                }
                pos ++
            }
            note.webLink = dato.substring(0, dato.length-1)
            alreadyAvailableNote = note
            setViewOrUpdate()
        }

        findViewById<View>(R.id.imageRemoveWebURL).setOnClickListener{
            textWebURL.setText(null)
            layoutWebURL.visibility = View.GONE
        }

        findViewById<View>(R.id.imageRemoveImage).setOnClickListener{
            imageNote.setImageBitmap(null)
            imageNote.visibility = View.GONE
            findViewById<View>(R.id.imageRemoveImage).visibility = View.GONE
            selectedImagePath = ""
        }

        initMiscellaneous()
        setSubtitleIndicatorColor()
    }

    private fun setViewOrUpdate(){
        inputNoteTitle.setText(alreadyAvailableNote!!.title)
        inputNoteSubtitle.setText(alreadyAvailableNote!!.subtitle)
        inputNoteText.setText(alreadyAvailableNote!!.noteText)
        textDateTime.setText(alreadyAvailableNote!!.dateTime)
        if(alreadyAvailableNote!!.imagePath != null && !alreadyAvailableNote!!.imagePath.trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote!!.imagePath))
            imageNote.visibility = View.VISIBLE
            findViewById<View>(R.id.imageRemoveImage).visibility = View.VISIBLE
            selectedImagePath = alreadyAvailableNote!!.imagePath
        }

        if(alreadyAvailableNote!!.webLink != null && !alreadyAvailableNote!!.webLink.trim().isEmpty()){
            textWebURL.setText(alreadyAvailableNote!!.webLink)
            layoutWebURL.visibility = View.VISIBLE
        }
    }

    private fun saveNote(){
        if(inputNoteTitle.text.toString().trim().isEmpty()){
            Toast.makeText(this, "El titulo de la nota no puede quedar vacio", Toast.LENGTH_SHORT).show()
            return
        }
        else if(inputNoteSubtitle.text.toString().trim().isEmpty() &&
                inputNoteText.text.toString().trim().isEmpty()){
            Toast.makeText(this, "La nota no puede quedar vacia", Toast.LENGTH_SHORT).show()
            return
        }

        var note = Note(
            uid = 0,
            title = inputNoteTitle.text.toString(),
            subtitle = inputNoteSubtitle.text.toString(),
            noteText = inputNoteText.text.toString(),
            dateTime = textDateTime.text.toString(),
            color = selectdNoteColor,
            imagePath = selectedImagePath,
            webLink = ""
        )

        if(layoutWebURL.visibility == View.VISIBLE){
            note.webLink = textWebURL.text.toString()
        }

        if(alreadyAvailableNote != null){
            note.uid = alreadyAvailableNote!!.uid
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask : AsyncTask<Void, Void, Void>() {
            @Override
            override fun doInBackground(vararg params: Void): Void? {
                NotesDataBase.getDatabase(applicationContext)?.noteDao()?.insertNote(note)
                return null
            }

            @Override
             override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                var intent : Intent = intent
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        SaveNoteTask().execute()
    }

    private fun initMiscellaneous(){
        var layoutMiscellaneous = findViewById<LinearLayout>(R.id.layoutMiscellaneous)
        var bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous)
        layoutMiscellaneous.findViewById<View>(R.id.textMiscellaneous).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        var imageColor1 : ImageView = layoutMiscellaneous.findViewById(R.id.imageColor)
        var imageColor2 : ImageView = layoutMiscellaneous.findViewById(R.id.imageColor2)
        var imageColor3 : ImageView = layoutMiscellaneous.findViewById(R.id.imageColor3)
        var imageColor4 : ImageView = layoutMiscellaneous.findViewById(R.id.imageColor4)
        var imageColor5 : ImageView = layoutMiscellaneous.findViewById(R.id.imageColor5)

        layoutMiscellaneous.findViewById<View>(R.id.viewColor1).setOnClickListener {
            selectdNoteColor = "#333333"
            imageColor1.setImageResource(R.drawable.ic_done)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor2).setOnClickListener {
            selectdNoteColor = "#FDBE3B"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(R.drawable.ic_done)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor3).setOnClickListener {
            selectdNoteColor = "#FF4842"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(R.drawable.ic_done)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor4).setOnClickListener {
            selectdNoteColor = "#3A52Fc"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(R.drawable.ic_done)
            imageColor5.setImageResource(0)
            setSubtitleIndicatorColor()
        }

        layoutMiscellaneous.findViewById<View>(R.id.viewColor5).setOnClickListener {
            selectdNoteColor = "#000000"
            imageColor1.setImageResource(0)
            imageColor2.setImageResource(0)
            imageColor3.setImageResource(0)
            imageColor4.setImageResource(0)
            imageColor5.setImageResource(R.drawable.ic_done)
            setSubtitleIndicatorColor()
        }

        if(alreadyAvailableNote != null && alreadyAvailableNote!!.color != null && !alreadyAvailableNote!!.color.trim().isEmpty()){
            when (alreadyAvailableNote!!.color){
                "#FDBE3B" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor2).performClick()
                "#FF4842" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor3).performClick()
                "#3A52Fc" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor4).performClick()
                "#000000" -> layoutMiscellaneous.findViewById<View>(R.id.viewColor5).performClick()
            }
        }

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddImage).setOnClickListener{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            if(ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this, 
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }
            else{
                selectImage()
            }
        }

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddUrl).setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showAddURLDialog()
        }

        if(alreadyAvailableNote != null){
            layoutMiscellaneous.findViewById<View>(R.id.layoutDeleteNote).visibility = View.VISIBLE
            layoutMiscellaneous.findViewById<View>(R.id.layoutDeleteNote).setOnClickListener{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                showDeleteNoteDialog()
            }
        }

    }

    private fun showDeleteNoteDialog(){
        if(dialogDeleteNote == null){
            var builder = AlertDialog.Builder(this)
            var view : View = LayoutInflater.from(this).inflate(
                R.layout.layout_delete_notes,
                findViewById(R.id.layoutDeleteNoteContainer)
            )
            builder.setView(view)
            dialogDeleteNote = builder.create()
            if(dialogDeleteNote!!.window != null){
                dialogDeleteNote!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            view.findViewById<View>(R.id.textDeleteNote).setOnClickListener{
                @SuppressLint("StaticFieldLeak")
                class DeleteNoteTask : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg params: Void): Void? {
                        NotesDataBase.getDatabase(applicationContext)?.noteDao()?.delete(alreadyAvailableNote!!)
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        lateinit var intent : Intent
                        intent.putExtra("isNoteDeleted", true)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }

                DeleteNoteTask().execute()

            }

            view.findViewById<View>(R.id.textCancel).setOnClickListener{
                dialogDeleteNote!!.dismiss()
            }
        }

        dialogDeleteNote!!.show()
    }

    private fun setSubtitleIndicatorColor() {
        val gradientDrawable = viewSubtitleIndicator!!.background as GradientDrawable
        gradientDrawable.setColor(Color.parseColor(selectdNoteColor))
    }

    private fun selectImage(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.size > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage()
            }
            else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUri: Uri? = data.data
                if (selectedImageUri != null) {
                    try {
                        val inputStream = contentResolver.openInputStream(selectedImageUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        imageNote!!.setImageBitmap(bitmap)
                        imageNote!!.visibility = View.VISIBLE
                        findViewById<View>(R.id.imageRemoveImage).visibility = View.GONE

                        selectedImagePath = getPathFromUri(selectedImageUri)

                    } catch (exception: Exception) {
                        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getPathFromUri(contentUri : Uri) : String {
        var filePath : String
        var cursor : Cursor? = contentResolver.query(contentUri,  null, null, null, null)
        if(cursor == null){
            filePath = contentUri.path.toString()
        }
        else{
            cursor.moveToFirst()
            var index : Int = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    private fun showAddURLDialog(){
        if(dialogAddURL == null){
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            var view : View = LayoutInflater.from(this).inflate(
                R.layout.layout_add_url,
                findViewById(R.id.layoutAddUrlContainer)
            )
            builder.setView(view)

            dialogAddURL = builder.create()
            if(dialogAddURL!!.window != null){
                dialogAddURL!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            var inputURL : EditText = view.findViewById(R.id.inputURL)
            inputURL.requestFocus()

            view.findViewById<View>(R.id.textAdd).setOnClickListener{
                if(inputURL.text.toString().trim().isEmpty()){
                    Toast.makeText(this, "Enter URL", Toast.LENGTH_SHORT).show()
                }
                else if(!Patterns.WEB_URL.matcher(inputURL.text.toString()).matches()){
                    Toast.makeText(this, "Enter valid URL", Toast.LENGTH_SHORT).show()
                }
                else{
                    textWebURL.setText(inputURL.text.toString())
                    layoutWebURL.visibility = View.VISIBLE
                    dialogAddURL!!.dismiss()
                }
            }

            view.findViewById<View>(R.id.textCancel).setOnClickListener{
                dialogAddURL!!.dismiss()
            }
        }

        dialogAddURL!!.show()
    }
}