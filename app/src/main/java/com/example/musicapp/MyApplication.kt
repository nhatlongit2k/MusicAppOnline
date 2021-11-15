package com.example.musicapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class MyApplication : Application() {

    companion object{
        val CHANEL_ID : String = "channel_service_music_app"
    }
    override fun onCreate() {
        super.onCreate()

        createChanelNotification()
    }

    private fun createChanelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var chanel : NotificationChannel = NotificationChannel(CHANEL_ID, "channel_service_music_app", NotificationManager.IMPORTANCE_DEFAULT)
            chanel.setSound(null, null)
            var manager : NotificationManager = getSystemService(NotificationManager::class.java)
            if(manager != null){
                manager.createNotificationChannel(chanel)
            }
        }
    }
}