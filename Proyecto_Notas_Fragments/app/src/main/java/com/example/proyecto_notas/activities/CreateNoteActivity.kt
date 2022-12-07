package com.example.proyecto_notas.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.proyecto_notas.NotesDataBase
import com.example.proyecto_notas.R
import com.example.proyecto_notas.adapters.MediaAdapter
import com.example.proyecto_notas.databinding.ActivityCreateNoteBinding
import com.example.proyecto_notas.entities.Multimedia
import com.example.proyecto_notas.entities.Note
import com.example.proyecto_notas.listeners.MediaListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateNoteActivity : AppCompatActivity(), OnTouchListener, MediaListener {

    private lateinit var imageBack: ImageView

    private lateinit var inputNoteTitle: EditText
    private lateinit var inputNoteSubtitle: EditText
    private lateinit var inputNoteText: EditText
    private lateinit var textDateTime: TextView
    private lateinit var selectdNoteColor: String
    private lateinit var viewSubtitleIndicator: View

    private lateinit var btnPlayAudio : Button
    private lateinit var btnStopRecording : Button
    private var mStartRecording: Boolean = true
    private var recorder: MediaRecorder? = null
    private lateinit var audioPath: String

    private var player: MediaPlayer? = null

    private lateinit var mediaPath: String

    private lateinit var textWebURL: TextView
    private lateinit var layoutWebURL: LinearLayout

    //private lateinit var videoMedia : VideoView

    lateinit var photoURI: Uri
    lateinit var videoURI: Uri

    private val REQUEST_CODE_STORAGE_PERMISSION = 1
    private val REQUEST_CODE_SELECT_IMAGE = 2
    private val REQUEST_IMAGE_CAPTURE = 3
    private val REQUEST_VIDEO_CAPTURE = 4
    private val REQUEST_CODE_SHOW_MEDIA = 5
    private val REQUEST_CODE_ADD_MEDIA = 6
    private val REQUEST_CODE_SHOW_ESPECIFIC_MEDIA = 7
    private val REQUEST_CODE_ADD_MEDIA_ESPECIFIC = 8

    private var dialogAddURL: AlertDialog? = null
    private var dialogDeleteNote: AlertDialog? = null

    private var alreadyAvailableNote : Note? = null

    //lateinit var mediaController: MediaController

    lateinit var binding : ActivityCreateNoteBinding

    private var dialogAddDescription: AlertDialog? = null

    private lateinit var recyclerviewMedia : RecyclerView
    private lateinit var mediaList : MutableList<Multimedia>
    private lateinit var mediaAdapter : MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        binding = ActivityCreateNoteBinding.inflate(layoutInflater)

        imageBack = findViewById(R.id.imageBack)
        imageBack.setOnClickListener{
            onBackPressed()
        }
        lateinit var imageSave : ImageView
        imageSave = findViewById(R.id.imageSave)
        imageSave.setOnClickListener{
            saveNote()
        }

        inputNoteTitle = findViewById(R.id.inputNoteTitle)
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle)
        inputNoteText = findViewById(R.id.inputNote)
        textDateTime = findViewById(R.id.textDateTime)
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator)

        dataInitialize()
        recyclerviewMedia = findViewById(R.id.recyclerviewMedia)
        recyclerviewMedia.setLayoutManager(
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        )

        mediaAdapter = MediaAdapter(mediaList, this)
        recyclerviewMedia.adapter = mediaAdapter

        //mediaController = MediaController(this)
        //mediaController.setAnchorView(binding.root)

        btnPlayAudio = findViewById(R.id.btnPlayAudio)
        btnStopRecording = findViewById(R.id.btnStopRecording)

        textWebURL = findViewById(R.id.textWebURL)
        layoutWebURL = findViewById(R.id.layoutWebURL)

        textDateTime.setText(
            SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(Date())
        )

        selectdNoteColor = "#333333"

        if (intent.getBooleanExtra("IsViewOrUpdate", false)) {
            var note = Note(
                uid = 0,
                title = "",
                subtitle = "",
                noteText = "",
                dateTime = "",
                color = "",
                webLink = ""
            )

            var prueba = intent.getSerializableExtra("note").toString()
            var dato = ""
            var pos = 0
            var con = 0
            var cuenta = false
            while(pos < prueba.length){
                if(prueba[pos] == '=') cuenta = true
                else if(prueba[pos] == ',' || pos == prueba.length-1){
                    if(con != 2) {

                        if (con == 0) note.uid = dato.toInt()
                        else if (con == 1) note.title = dato
                        else if (con == 3) note.dateTime = dato
                        else if (con == 4) note.subtitle = dato
                        else if (con == 5) note.noteText = dato
                        else if (con == 6) note.color = dato
                        else if (con == 7) note.webLink = dato

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
            alreadyAvailableNote = note
            setViewOrUpdate()
        }

        findViewById<View>(R.id.imageRemoveWebURL).setOnClickListener{
            textWebURL.setText(null)
            layoutWebURL.visibility = View.GONE
        }

        btnStopRecording.setOnClickListener{
            stopRecording()
        }

        btnPlayAudio.setOnClickListener{
            onPlay(mStartRecording)
            mStartRecording = !mStartRecording
        }

        initMiscellaneous()
        setSubtitleIndicatorColor()
    }

    private fun getMedia(requestCode: Int, idNoteUpdateorView : Int) {
        var id = NotesDataBase.getDatabase(applicationContext)?.noteDao()?.getAllNotes()!!.size

        if(requestCode == REQUEST_CODE_SHOW_MEDIA){
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimedia(id + 1)

            mediaList.addAll(media!!)
            mediaAdapter.notifyDataSetChanged()
        }
        else if(requestCode == REQUEST_CODE_ADD_MEDIA){
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimedia(id + 1)
            var newMedia = media!!.get(media!!.size - 1)

            mediaList.add(newMedia)
            mediaAdapter.notifyDataSetChanged()
        }
        else if(requestCode == REQUEST_CODE_SHOW_ESPECIFIC_MEDIA){
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimedia(idNoteUpdateorView)

            mediaList.addAll(media!!)
            mediaAdapter.notifyDataSetChanged()
        }
        else if(requestCode == REQUEST_CODE_ADD_MEDIA_ESPECIFIC) {
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimedia(idNoteUpdateorView)
            var newMedia = media!!.get(media!!.size - 1)

            mediaList.add(newMedia)
            mediaAdapter.notifyDataSetChanged()
        }
    }

    private fun dataInitialize(){
        mediaList = ArrayList()
    }

    private fun setViewOrUpdate(){
        inputNoteTitle.setText(alreadyAvailableNote!!.title)
        inputNoteSubtitle.setText(alreadyAvailableNote!!.subtitle)
        inputNoteText.setText(alreadyAvailableNote!!.noteText)
        textDateTime.setText(alreadyAvailableNote!!.dateTime)

        if(alreadyAvailableNote!!.webLink != null && !alreadyAvailableNote!!.webLink.trim().isEmpty()){
            textWebURL.setText(alreadyAvailableNote!!.webLink)
            layoutWebURL.visibility = View.VISIBLE
        }

        getMedia(REQUEST_CODE_SHOW_ESPECIFIC_MEDIA, alreadyAvailableNote!!.uid)
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

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddPhoto).setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            takePhoto()
        }

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddVideo).setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            takeVideo()
        }

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddAudio).setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            grabar()
            iniciarGraabacion()
        }

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddReminder).visibility = View.GONE

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

    @SuppressLint("QueryPermissionsNeeded")
    private fun selectImage(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takePhoto(){
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = saveImage()
            } catch (ex: IOException) {
                null
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.proyecto_notas.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun takeVideo(){
        var intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            var videoFile: File? = null
            try {
                videoFile = saveVideo()
            } catch (ex: IOException) {
                Log.e("error", ex.toString())
            }

            if (videoFile != null) {
                videoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.proyecto_notas.fileprovider",
                    videoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
            }
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            showAddDescriptionDialog(REQUEST_IMAGE_CAPTURE)
        }
        else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            showAddDescriptionDialog(REQUEST_VIDEO_CAPTURE)
        }
        else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUri: Uri? = data.data
                if (selectedImageUri != null) {
                    try {
                        mediaPath = getPathFromUri(selectedImageUri)
                        showAddDescriptionDialog(REQUEST_IMAGE_CAPTURE)
                    } catch (exception: Exception) {
                        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun saveImage(): File? {
        val nombreArchivo = "foto_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File.createTempFile(nombreArchivo, ".jpg", directorio)
        mediaPath = archivo.absolutePath
        return archivo
    }

    @Throws(IOException::class)
    private fun saveVideo(): File? {
        val nombreArchivo = "video_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File.createTempFile(nombreArchivo, ".mp4", directorio)
        mediaPath = archivo.absolutePath
        return archivo
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

    private fun showAddDescriptionDialog(mediaType : Int){
        if(dialogAddDescription == null) {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var view: View = LayoutInflater.from(this).inflate(
                R.layout.layout_add_description,
                findViewById(R.id.layoutAddDescriptionContainer)
            )
            builder.setView(view)

            dialogAddDescription = builder.create()
            if (dialogAddDescription!!.window != null) {
                dialogAddDescription!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            var inputDescription: EditText = view.findViewById(R.id.inputDescription)
            inputDescription.requestFocus()

            view.findViewById<View>(R.id.textAddDescription).setOnClickListener {
                if(mediaType == REQUEST_IMAGE_CAPTURE) saveMediaOnDatabase(inputDescription.text.toString(), REQUEST_IMAGE_CAPTURE)
                else if(mediaType == REQUEST_VIDEO_CAPTURE) saveMediaOnDatabase(inputDescription.text.toString(), REQUEST_VIDEO_CAPTURE)
                else saveMediaOnDatabase(inputDescription.text.toString(), 3)

                inputDescription.setText("")
                mediaPath = ""
                dialogAddDescription!!.dismiss()
            }

            view.findViewById<View>(R.id.textCancelDescription).setOnClickListener {
                inputDescription.setText("")
                mediaPath = ""
                dialogAddDescription!!.dismiss()
            }
        }
        dialogAddDescription!!.show()
    }

    private fun saveMediaOnDatabase(description: String, action : Int) {
        var id = NotesDataBase.getDatabase(applicationContext)?.noteDao()?.getAllNotes()!!.size
        var type = ""
        if(action == REQUEST_IMAGE_CAPTURE) type = "photo"
        else if(action == REQUEST_VIDEO_CAPTURE) type = "video"
        val media = Multimedia(
            idMultimedia = 0,
            idNote = id + 1,
            idTask = 0,
            type = type,
            uri = mediaPath,
            description = description
        )

        if(media.uri.substring(media.uri.length-3) == "mp4") media.type = "video"
        else media.type = "photo"

        if(alreadyAvailableNote != null){
            media.idNote = alreadyAvailableNote!!.uid
            NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.insert(media)
            getMedia(REQUEST_CODE_ADD_MEDIA_ESPECIFIC, alreadyAvailableNote!!.uid)
        }
        else{
            NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.insert(media)
            getMedia(REQUEST_CODE_ADD_MEDIA, 0)
        }
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

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        //mediaController.show()
        return false
    }

    private fun grabar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            revisarPermisos()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun revisarPermisos() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                "android.permission.RECORD_AUDIO"
            ) == PackageManager.PERMISSION_GRANTED -> {}
            shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") -> {

                MaterialAlertDialogBuilder(this)
                    .setTitle("Title")
                    .setMessage("Debes dar perimso para grabar audios")
                    .setNegativeButton("Cancel") { dialog, which ->
                    }
                    .setPositiveButton("OK") { dialog, which ->
                        requestPermissions(
                            arrayOf("android.permission.RECORD_AUDIO",
                                "android.permission.WRITE_EXTERNAL_STORAGE"),
                            1001)
                    }
                    .show()
            }
            else -> {
                requestPermissions(
                    arrayOf("android.permission.RECORD_AUDIO",
                        "android.permission.WRITE_EXTERNAL_STORAGE"),
                    1001)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun iniciarGraabacion() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            saveAudio()
            setOutputFile(audioPath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    @SuppressLint("RestrictedApi")
    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(audioPath)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    @Throws(IOException::class)
    fun saveAudio(): File {
        val nombreArchivo = "audio_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File.createTempFile(nombreArchivo, ".mp3", directorio)
        audioPath = archivo.absolutePath
        return archivo
    }

    override fun onMediaClicked(media: Multimedia?, position: Int) {
        if(media!!.type == "video"){

        }
        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT).show()
    }

}