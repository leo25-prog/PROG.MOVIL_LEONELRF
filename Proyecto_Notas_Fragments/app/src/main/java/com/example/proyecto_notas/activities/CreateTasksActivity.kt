package com.example.proyecto_notas.activities

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.PackageManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.proyecto_notas.NotesDataBase
import com.example.proyecto_notas.R
import com.example.proyecto_notas.adapters.MediaAdapter
import com.example.proyecto_notas.databinding.ActivityCreateTasksBinding
import com.example.proyecto_notas.entities.Multimedia
import com.example.proyecto_notas.entities.Reminder
import com.example.proyecto_notas.entities.Task
import com.example.proyecto_notas.listeners.MediaListener
import com.example.proyecto_notas.reciver.MiReceiverAlarm
import com.example.proyecto_notas.reciver.notificationID
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

class CreateTasksActivity : AppCompatActivity(), MediaListener {

    private lateinit var imageBackTask: ImageView
    private lateinit var imageSaveTask: ImageView

    private lateinit var inputTaskTitle: EditText
    private lateinit var btnDate : Button
    private lateinit var btnTime : Button
    private lateinit var dateText : TextView
    private lateinit var timeText : TextView
    private lateinit var spinnerPriority: Spinner
    private lateinit var btnDateReminder : Button
    private lateinit var btnTimeReminder : Button
    private lateinit var timeTextReminder : TextView
    private lateinit var dateTextReminder : TextView
    private lateinit var btnAddReminder : Button

    private lateinit var btnPlayAudioTask : Button
    private lateinit var btnStopRecordingTask : Button
    private var mStartRecordingTask: Boolean = true
    private var recorderTask: MediaRecorder? = null
    private lateinit var audioPathTask: String
    private var playerTask: MediaPlayer? = null

    private var timestamp: Long = 0
    private var dia : Int = 0
    private var mes : Int = 0
    private var anio : Int = 0
    private var hora : Int = 0
    private var minuto : Int = 0

    private var diaReminder : Int = 0
    private var mesReminder : Int = 0
    private var anioReminder : Int = 0
    private var horaReminder : Int = 0
    private var minutoReminder : Int = 0

    lateinit var photoURITask: Uri
    lateinit var videoURITask: Uri
    private lateinit var mediaPathTask: String

    private lateinit var recyclerviewMediaTask : RecyclerView
    private lateinit var mediaListTask : MutableList<Multimedia>
    private lateinit var mediaAdapterTask : MediaAdapter

    private val REQUEST_CODE_STORAGE_PERMISSION_TASK = 9
    private val REQUEST_CODE_SELECT_IMAGE_TASK = 10
    private val REQUEST_IMAGE_CAPTURE_TASK = 11
    private val REQUEST_VIDEO_CAPTURE_TASK = 12
    private val REQUEST_CODE_SHOW_MEDIA_TASK = 13
    private val REQUEST_CODE_ADD_MEDIA_TASK = 14
    private val REQUEST_CODE_SHOW_ESPECIFIC_MEDIA_TASK = 15
    private val REQUEST_CODE_ADD_MEDIA_ESPECIFIC_TASK = 16

    private var dialogDeleteTask: AlertDialog? = null
    private var dialogAddDescriptionTask: AlertDialog? = null

    private var alreadyAvailableTask : Task? = null

    lateinit var binding : ActivityCreateTasksBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tasks)

        imageBackTask = findViewById(R.id.imageBackTask)
        imageBackTask.setOnClickListener{
            onBackPressed()
        }

        imageSaveTask = findViewById(R.id.imageSaveTask)
        imageSaveTask.setOnClickListener{
            saveTask()
        }

        inputTaskTitle = findViewById(R.id.edt_task)

        btnDate = findViewById(R.id.btnDate)
        btnDate.setOnClickListener{
            selectedDate(false)
        }

        dateText = findViewById(R.id.dateText)

        btnTime = findViewById(R.id.btnTime)
        btnTime.setOnClickListener{
            selectedTime(false)
        }

        timeText = findViewById(R.id.timeText)

        spinnerPriority = findViewById(R.id.spinnerPriority)

        btnDateReminder = findViewById(R.id.btnDateReminder)
        btnDateReminder.setOnClickListener{
            selectedDate(true)
        }

        dateTextReminder = findViewById(R.id.dateTextReminder)

        btnTimeReminder = findViewById(R.id.btnTimeReminder)
        btnTimeReminder.setOnClickListener{
            selectedTime(true)
        }

        timeTextReminder = findViewById(R.id.timeTextReminder)

        btnAddReminder = findViewById(R.id.btnReminder)
        btnAddReminder.setOnClickListener{
            if(inputTaskTitle.text.toString() != "") {
                val id = NotesDataBase.getDatabase(applicationContext)!!.taskDao()!!.getAllTask().size

                //Notification
                scheduleNotification(inputTaskTitle.text.toString())

                //Insert Notification
                lifecycleScope.launch {
                    val newReminder = Reminder(
                        0,
                        id+1,
                        "$diaReminder/$mesReminder/$anioReminder",
                        "$horaReminder:$minutoReminder"
                    )
                    NotesDataBase.getDatabase(applicationContext)!!.reminderDao().insert(newReminder)
                }

                dateTextReminder.text = ""
                timeTextReminder.text = ""

                var reminder = findViewById<View>(R.id.layoutReminderConfigure)
                reminder.visibility = View.GONE
            }
            else{
                Toast.makeText(this, "Define a title for the task", Toast.LENGTH_SHORT).show()
            }

        }

        dataInitialize()
        recyclerviewMediaTask = findViewById(R.id.recyclerviewMediaTask)
        recyclerviewMediaTask.setLayoutManager(
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        )

        mediaAdapterTask = MediaAdapter(mediaListTask, this)
        recyclerviewMediaTask.adapter = mediaAdapterTask

        btnPlayAudioTask = findViewById(R.id.btnPlayAudioTask)
        btnStopRecordingTask = findViewById(R.id.btnStopRecordingTask)

        if (intent.getBooleanExtra("IsViewOrUpdate", false)) {
            var task = Task(
                id = 0,
                title = "",
                priority = "",
                timestamp = 0,
                limitDate = "",
                limitTime = ""
            )

            var prueba = intent.getSerializableExtra("task").toString()
            Log.d("My_notes", prueba.toString())
            var dato = ""
            var pos = 0
            var con = 0
            var cuenta = false
            while(pos < prueba.length){
                if(prueba[pos] == '=') cuenta = true
                else if(prueba[pos] == ',' || pos == prueba.length-1){

                    if (con == 0) task.id = dato.toInt()
                    else if (con == 1) task.title = dato
                    else if (con == 2) task.priority = dato
                    else if (con == 3) task.timestamp = dato.toLong()
                    else if (con == 4) task.limitDate = dato
                    else if (con == 5) task.limitTime = dato

                    cuenta = false
                    dato = ""
                    con ++
                }
                else if(cuenta){
                    dato += prueba[pos]
                }
                pos ++
            }
            alreadyAvailableTask = task
            Log.d("My_notes",alreadyAvailableTask!!.limitDate + "  " + alreadyAvailableTask!!.limitTime)
            setViewOrUpdate()
        }

        btnStopRecordingTask.setOnClickListener{
            stopRecording()
        }

        btnPlayAudioTask.setOnClickListener{
            onPlay(mStartRecordingTask)
            mStartRecordingTask = !mStartRecordingTask
        }

        initMiscellaneous()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(titulo: String) {
        getTime(titulo)
    }

    private fun getTime(titulo: String){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, horaReminder)
        calendar.set(Calendar.MINUTE, minutoReminder)
        calendar.set(Calendar.SECOND,0)
        startAlarm(calendar, titulo)
    }

    private fun startAlarm(calendar: Calendar, titulo: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MiReceiverAlarm::class.java)
        intent.action = titulo
        val pendingIntent = PendingIntent.getBroadcast(this, notificationID, intent, PendingIntent.FLAG_MUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }


    private fun getMedia(requestCode: Int, idNoteUpdateorView : Int) {
        var id = NotesDataBase.getDatabase(applicationContext)?.taskDao()?.getAllTask()!!.size

        if(requestCode == REQUEST_CODE_SHOW_MEDIA_TASK){
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimediaTask(id + 1)

            mediaListTask.addAll(media!!)
            mediaAdapterTask.notifyDataSetChanged()
        }
        else if(requestCode == REQUEST_CODE_ADD_MEDIA_TASK){
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimediaTask(id + 1)
            var newMedia = media!!.get(media!!.size - 1)

            mediaListTask.add(newMedia)
            mediaAdapterTask.notifyDataSetChanged()
        }
        else if(requestCode == REQUEST_CODE_SHOW_ESPECIFIC_MEDIA_TASK){
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimediaTask(idNoteUpdateorView)

            mediaListTask.addAll(media!!)
            mediaAdapterTask.notifyDataSetChanged()
        }
        else if(requestCode == REQUEST_CODE_ADD_MEDIA_ESPECIFIC_TASK) {
            var media = NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.getMultimediaTask(idNoteUpdateorView)
            var newMedia = media!!.get(media!!.size - 1)

            mediaListTask.add(newMedia)
            mediaAdapterTask.notifyDataSetChanged()
        }
    }

    private fun dataInitialize(){
        mediaListTask = ArrayList()
    }

    private fun selectedDate(reminder : Boolean){
        var calendar = Calendar.getInstance()
        dia = calendar.get(Calendar.DAY_OF_MONTH)
        mes = calendar.get(Calendar.MONTH)
        anio = calendar.get(Calendar.YEAR)

        if(!reminder) {
            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth -> dateText.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year) },
                anio, mes, dia
            )
            datePickerDialog.show()
        }
        else{
            val datePickerDialog = DatePickerDialog(
                this,
                {
                    view, year, monthOfYear, dayOfMonth -> dateTextReminder.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    this.anioReminder = year
                    this.mesReminder = monthOfYear
                    this.diaReminder = dayOfMonth
                },
                anio, mes, dia
            )
            datePickerDialog.show()
        }
    }

    private fun selectedTime(reminder : Boolean){
        var calendar = Calendar.getInstance()
        hora = calendar.get(Calendar.HOUR_OF_DAY)
        minuto = calendar.get(Calendar.MINUTE)

        if(!reminder) {
            val timePickerDialog = TimePickerDialog(
                this,
                { view, hour, minutes -> timeText.setText(hour.toString() + ":" + minutes) },
                hora, minuto, false
            )
            timePickerDialog.show()
        }
        else{
            val timePickerDialog = TimePickerDialog(
                this,
                {
                    view, hour, minutes -> timeTextReminder.setText(hour.toString() + ":" + minutes)
                    this.horaReminder = hour
                    this.minutoReminder = minutes
                },
                hora, minuto, false
            )
            timePickerDialog.show()
        }
    }

    private fun setViewOrUpdate(){
        inputTaskTitle.setText(alreadyAvailableTask!!.title)
        dateText.text = alreadyAvailableTask!!.limitDate
        timeText.text = alreadyAvailableTask!!.limitTime

        var pos = 0
        if(alreadyAvailableTask!!.priority == "High priority") pos = 0
        else if(alreadyAvailableTask!!.priority == "Medium priority") pos = 1
        else pos = 2

        spinnerPriority.setSelection(pos)
        timestamp = alreadyAvailableTask!!.timestamp

        getMedia(REQUEST_CODE_SHOW_ESPECIFIC_MEDIA_TASK, alreadyAvailableTask!!.id)
    }

    private fun saveTask() {
        if(inputTaskTitle.text.toString().trim().isEmpty()){
            Toast.makeText(this, "La tarea no puede quedar vacia", Toast.LENGTH_SHORT).show()
            return
        }

        var priority = ""
        if(spinnerPriority.selectedItemPosition == 0) priority = "High priority"
        else if(spinnerPriority.selectedItemPosition == 1) priority = "Medium priority"
        else priority = "Low priority"

        var task = Task(
            id = 0,
            title = inputTaskTitle.text.toString(),
            priority = priority,
            timestamp = System.currentTimeMillis(),
            limitDate = dateText.text.toString(),
            limitTime = timeText.text.toString()
        )

        if(alreadyAvailableTask != null){
            task.id = alreadyAvailableTask!!.id
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask : AsyncTask<Void, Void, Void>() {
            @Override
            override fun doInBackground(vararg params: Void): Void? {
                NotesDataBase.getDatabase(applicationContext)?.taskDao()?.insertTask(task)
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
        layoutMiscellaneous.visibility = View.VISIBLE
        var bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous)
        layoutMiscellaneous.findViewById<View>(R.id.textMiscellaneous).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        layoutMiscellaneous.findViewById<View>(R.id.layoutNoteColor).visibility = View.GONE
        layoutMiscellaneous.findViewById<View>(R.id.layoutAddUrl).visibility = View.GONE

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddImage).setOnClickListener{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            if(ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION_TASK
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

        layoutMiscellaneous.findViewById<View>(R.id.layoutAddReminder).setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            var reminder = findViewById<View>(R.id.layoutReminderConfigure)
            reminder.visibility = View.VISIBLE
        }

        if(alreadyAvailableTask != null) {
            layoutMiscellaneous.findViewById<View>(R.id.layoutDeleteTask).visibility = View.VISIBLE
            layoutMiscellaneous.findViewById<View>(R.id.layoutDeleteTask).setOnClickListener {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                showDeleteTaskDialog()
            }
        }
    }

    private fun showDeleteTaskDialog(){
        if(dialogDeleteTask == null){
            var builder = AlertDialog.Builder(this)
            var view : View = LayoutInflater.from(this).inflate(
                R.layout.layout_delete_task,
                findViewById(R.id.layoutDeleteTaskContainer)
            )

            builder.setView(view)
            dialogDeleteTask = builder.create()
            if(dialogDeleteTask!!.window != null){
                dialogDeleteTask!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            view.findViewById<View>(R.id.textDeleteTask).setOnClickListener{
                @SuppressLint("StaticFieldLeak")
                class DeleteTasksTask : AsyncTask<Void, Void, Void>() {

                    override fun doInBackground(vararg params: Void): Void? {
                        NotesDataBase.getDatabase(applicationContext)?.taskDao()?.delete(alreadyAvailableTask!!)
                        return null
                    }

                    override fun onPostExecute(result: Void?) {
                        super.onPostExecute(result)
                        lateinit var intent : Intent
                        intent.putExtra("isTaskDeleted", true)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }

                DeleteTasksTask().execute()
            }

            view.findViewById<View>(R.id.textCancel).setOnClickListener{
                dialogDeleteTask!!.dismiss()
            }
        }

        dialogDeleteTask!!.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun selectImage(){
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_TASK)
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
                photoURITask = FileProvider.getUriForFile(
                    this,
                    "com.example.proyecto_notas.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURITask)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE_TASK)
            }
        }
    }

    @Throws(IOException::class)
    private fun saveImage(): File? {
        val nombreArchivo = "foto_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File.createTempFile(nombreArchivo, ".jpg", directorio)
        mediaPathTask = archivo.absolutePath
        return archivo
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
                videoURITask = FileProvider.getUriForFile(
                    this,
                    "com.example.proyecto_notas.fileprovider",
                    videoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoURITask)
                startActivityForResult(intent, REQUEST_VIDEO_CAPTURE_TASK)
            }
        }
    }

    @Throws(IOException::class)
    private fun saveVideo(): File? {
        val nombreArchivo = "video_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File.createTempFile(nombreArchivo, ".mp4", directorio)
        mediaPathTask = archivo.absolutePath
        return archivo
    }

    private fun showAddDescriptionDialog(mediaType : Int){
        if(dialogAddDescriptionTask == null) {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var view: View = LayoutInflater.from(this).inflate(
                R.layout.layout_add_description,
                findViewById(R.id.layoutAddDescriptionContainer)
            )
            builder.setView(view)

            dialogAddDescriptionTask = builder.create()
            if (dialogAddDescriptionTask!!.window != null) {
                dialogAddDescriptionTask!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            var inputDescription: EditText = view.findViewById(R.id.inputDescription)
            inputDescription.requestFocus()

            view.findViewById<View>(R.id.textAddDescription).setOnClickListener {
                if(mediaType == REQUEST_IMAGE_CAPTURE_TASK) saveMediaOnDatabase(inputDescription.text.toString(), REQUEST_IMAGE_CAPTURE_TASK)
                else if(mediaType == REQUEST_VIDEO_CAPTURE_TASK) saveMediaOnDatabase(inputDescription.text.toString(), REQUEST_VIDEO_CAPTURE_TASK)
                else saveMediaOnDatabase(inputDescription.text.toString(), 3)

                inputDescription.setText("")
                mediaPathTask = ""
                Log.d("My_notes", mediaPathTask)
                dialogAddDescriptionTask!!.dismiss()
            }

            view.findViewById<View>(R.id.textCancelDescription).setOnClickListener {
                inputDescription.setText("")
                mediaPathTask = ""
                dialogAddDescriptionTask!!.dismiss()
            }
        }
        dialogAddDescriptionTask!!.show()
    }

    private fun saveMediaOnDatabase(description: String, action : Int) {
        var id = NotesDataBase.getDatabase(applicationContext)?.taskDao()?.getAllTask()!!.size
        var type = ""
        if(action == REQUEST_IMAGE_CAPTURE_TASK) type = "photo"
        else if(action == REQUEST_VIDEO_CAPTURE_TASK) type = "video"
        val media = Multimedia(
            idMultimedia = 0,
            idNote = 0,
            idTask = id + 1,
            type = type,
            uri = mediaPathTask,
            description = description
        )

        Log.d("My_notes", "SALE2 + " + mediaPathTask)
        Log.d("My_notes", "SALE + " + media.uri)

        if(media.uri.substring(media.uri.length-3) == "mp4") media.type = "video"
        else media.type = "photo"

        if(alreadyAvailableTask != null){
            media.idTask = alreadyAvailableTask!!.id
            NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.insert(media)
            getMedia(REQUEST_CODE_ADD_MEDIA_ESPECIFIC_TASK, alreadyAvailableTask!!.id)
        }
        else{
            NotesDataBase.getDatabase(applicationContext)?.multimediaDao()?.insert(media)
            getMedia(REQUEST_CODE_ADD_MEDIA_TASK, 0)
        }
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
        recorderTask = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            saveAudio()
            setOutputFile(audioPathTask)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(PackageManagerCompat.LOG_TAG, "prepare() failed")
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
        playerTask?.release()
        playerTask = null
    }

    @SuppressLint("RestrictedApi")
    private fun startPlaying() {
        playerTask = MediaPlayer().apply {
            try {
                setDataSource(audioPathTask)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(PackageManagerCompat.LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopRecording() {
        recorderTask?.apply {
            stop()
            release()
        }
        recorderTask = null
    }

    @Throws(IOException::class)
    fun saveAudio(): File {
        val nombreArchivo = "audio_"
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File.createTempFile(nombreArchivo, ".mp3", directorio)
        audioPathTask = archivo.absolutePath
        return archivo
    }

    override fun onMediaClicked(media: Multimedia?, position: Int) {
        if(media!!.type == "video"){

        }
        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION_TASK && grantResults.size > 0){
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

        if (requestCode == REQUEST_IMAGE_CAPTURE_TASK && resultCode == RESULT_OK) {
            showAddDescriptionDialog(REQUEST_IMAGE_CAPTURE_TASK)
        }
        else if (requestCode == REQUEST_VIDEO_CAPTURE_TASK && resultCode == RESULT_OK){
            showAddDescriptionDialog(REQUEST_VIDEO_CAPTURE_TASK)
        }
        else if (requestCode == REQUEST_CODE_SELECT_IMAGE_TASK && resultCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUri: Uri? = data.data
                if (selectedImageUri != null) {
                    try {
                        mediaPathTask = getPathFromUri(selectedImageUri)
                        Log.d("My_notes", "RARO " + mediaPathTask)
                        showAddDescriptionDialog(REQUEST_IMAGE_CAPTURE_TASK)
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

}



