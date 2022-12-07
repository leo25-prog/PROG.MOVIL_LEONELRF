package com.example.proyecto_notas.reciver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.proyecto_notas.activities.MainActivity


var notificationID = 1

class MiReceiverAlarm : BroadcastReceiver() {

    var titulo = "Pendiente"
    override fun onReceive(context: Context, intent: Intent ) {
        intent.action?.let{ datos(it) }
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder(titulo).build()
        notificationUtils.getManager().notify(notificationID++, notification)
    }

    private fun datos(info: String){
        this.titulo = info
    }

}