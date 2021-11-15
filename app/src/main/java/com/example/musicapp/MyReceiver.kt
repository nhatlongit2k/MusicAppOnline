package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        var actionMusic: Int? = p1?.getIntExtra("action_music", 0)
        var intent: Intent = Intent(p0, MyService::class.java)
        intent.putExtra("action_music_service", actionMusic)
        p0?.startService(intent)
    }
}