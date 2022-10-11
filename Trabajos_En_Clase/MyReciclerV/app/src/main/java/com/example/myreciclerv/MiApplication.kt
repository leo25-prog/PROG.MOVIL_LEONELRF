package com.example.myreciclerv

import android.app.Application
import com.example.myreciclerv.data.Flower.flowerList

class MiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DataSource.lsFlower.addAll(flowerList(resources))
    }

}