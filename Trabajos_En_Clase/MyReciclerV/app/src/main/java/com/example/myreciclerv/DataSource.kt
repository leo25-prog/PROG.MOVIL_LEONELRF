package com.example.myreciclerv

import android.content.Context

class DataSource (val contexto : Context){
    fun getFlowerList() : Array <String> {
        return contexto.resources.getStringArray(R.array.flower_array)
    }
}