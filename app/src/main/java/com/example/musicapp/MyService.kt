package com.example.musicapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicapp.MyApplication.Companion.CHANEL_ID
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class MyService : Service() {

    companion object{
        val ACTION_STOP: Int = 12
        val ACTION_PAUSE: Int = 1
        val ACTION_RESUME: Int = 2
        val ACTION_NEXT: Int = 3
        val ACTION_PREVIOUS: Int = 4
        val ACTION_START: Int = 5
        val ACTION_SEEK_BAR = 6
        val ACTION_NO_REPEAT = 7
        val ACTION_REPEAT = 8
        val ACTION_REPEAT_ONE_SONG = 9
        val ACTION_SHUFFLE = 10
        val ACTION_NO_SHUFFLE = 11
        val ACTION_UPDATE_CURRENT_TIME = 99
    }
    var isPlaying: Boolean = false
    var mediaPlayer: MediaPlayer? = null
    var coppyOfListSong : ArrayList<File> = ArrayList<File>()
    var listSong : ArrayList<File> = ArrayList<File>()
    var song_uri: String =""
    var position: Int = 0
    lateinit var request: Job

    var timeOfSeekBar: Int? = 0
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var bundle: Bundle? = intent?.extras
        if(bundle != null){
//            listSong = intent?.getSerializableExtra("list_song") as ArrayList<File>
            var get_song_uri: String? = intent?.getStringExtra("song_uri")
            if(get_song_uri != null){
                position = bundle.getInt("position", 0)
                song_uri = get_song_uri
                listSong = intent?.getSerializableExtra("list_song") as ArrayList<File>
                coppyOfListSong.addAll(listSong)
                startMusic(song_uri)
                sendNotification(song_uri)
            }
//            if(listSong != null){
//                startMusic(listSong[position].toString())
//                sendNotification(listSong[position].toString())
//            }
        }
        var actionMusic: Int? = intent?.getIntExtra("action_music_service", 0)
        if (actionMusic != null) {
            if(actionMusic == ACTION_SEEK_BAR){
                timeOfSeekBar = intent?.getIntExtra("time_of_seekbar", 0)
            }
            handleActionMusic(actionMusic)
        }

        request = GlobalScope.launch {
            while (isPlaying == true){
                sendActionToActivity(ACTION_UPDATE_CURRENT_TIME)
                delay(1000)
            }
        }

//        var handler: Handler = Handler()
//        handler.postDelayed(object :Runnable {
//            override fun run() {
//                while (isPlaying == true){
//                    sendActionToActivity(ACTION_UPDATE_CURRENT_TIME)
//                    handler.postDelayed(this, 1000)
//                }
//            }
//        },1000)

//        runBlocking {
//            request = launch(Dispatchers.Default){
//                while (isPlaying == true){
//                    sendActionToActivity(ACTION_UPDATE_CURRENT_TIME)
//                    delay(1000)
//                }
//                if(isPlaying == false){
//                    request.cancel()
//                }
//            }
//        }

//        sendNotification()
        return START_NOT_STICKY
    }


    private fun startMusic(song: String) {
        var uri: Uri = Uri.parse(song)
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
        }
        mediaPlayer?.start()
        isPlaying = true
        mediaPlayer?.setOnCompletionListener {
            nextMusic()
        }
        sendActionToActivity(ACTION_START)
    }

    private fun handleActionMusic(action: Int){
        when(action){
            ACTION_STOP->{
                request.cancel()
                stopSelf()
            }
            ACTION_PAUSE->{
                pauseMusic()
            }
            ACTION_RESUME->{
                resumeMusic()
            }
            ACTION_NEXT->{
                nextMusic()
            }
            ACTION_PREVIOUS->{
                previousMusic()
            }
            ACTION_SEEK_BAR->{
                setTimeSong()
            }
            ACTION_NO_REPEAT->{
                mediaPlayer?.setOnCompletionListener {
                    position ++
                    if(position>=listSong.size-1 ){
                        stopSelf()
                    }else{
                        var uri: Uri = Uri.parse(listSong[position].toString())
                        mediaPlayer?.stop()
                        mediaPlayer = null
                        mediaPlayer = MediaPlayer.create(applicationContext, uri)
                        mediaPlayer?.start()
                        isPlaying = true
                        sendNotification(listSong[position].name.toString())
                        sendActionToActivity(ACTION_NEXT)
                    }
                }
            }
            ACTION_REPEAT->{
                mediaPlayer?.setOnCompletionListener {
                    nextMusic()
                }
            }
            ACTION_REPEAT_ONE_SONG->{
                mediaPlayer?.setOnCompletionListener {
                    var uri: Uri = Uri.parse(listSong[position].toString())
                    mediaPlayer?.stop()
                    mediaPlayer = null
                    mediaPlayer = MediaPlayer.create(applicationContext, uri)
                    mediaPlayer?.start()
                    isPlaying = true
                    sendNotification(listSong[position].name.toString())
                    sendActionToActivity(ACTION_NEXT)
                }
            }
            ACTION_SHUFFLE ->{
                var file = listSong[position]
                Collections.shuffle(listSong)
                Collections.swap(listSong, 0, listSong.indexOf(file))
                position = 0
            }
            ACTION_NO_SHUFFLE ->{
                var file = listSong[position]
                listSong.clear()
                listSong.addAll(coppyOfListSong)
                position = listSong.indexOf(file)
            }
        }
    }

    private fun setTimeSong() {
        mediaPlayer?.seekTo(timeOfSeekBar!!)
    }


    private fun previousMusic() {
        if(position <= 0){
            position = listSong.size-1
        }else{
            position--
        }
        var uri: Uri = Uri.parse(listSong[position].toString())
        mediaPlayer?.stop()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(applicationContext, uri)
        mediaPlayer?.start()
        isPlaying = true
        sendNotification(listSong[position].name.toString())
        sendActionToActivity(ACTION_PREVIOUS)
    }

    private fun nextMusic() {
        if(position >= listSong.size - 1){
            position = 0
        }else{
            position++
        }
        var uri: Uri = Uri.parse(listSong[position].toString())
        mediaPlayer?.stop()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(applicationContext, uri)
        mediaPlayer?.start()
        isPlaying = true
        sendNotification(listSong[position].name.toString())
        sendActionToActivity(ACTION_NEXT)
    }

    private fun resumeMusic() {
        if(mediaPlayer!=null && !isPlaying){
            mediaPlayer!!.start()
            isPlaying = true
            sendNotification(listSong[position].name.toString())
            sendActionToActivity(ACTION_RESUME)
        }
    }

    private fun pauseMusic(){
        if(mediaPlayer != null && isPlaying){
            mediaPlayer!!.pause()
            isPlaying = false
            sendNotification(listSong[position].name.toString())
            sendActionToActivity(ACTION_PAUSE)
        }
    }

    private fun sendNotification(song: String) {
//        var intent: Intent = Intent(this, PlayMusicActivity::class.java)
//        var pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        var remoteViews: RemoteViews = RemoteViews(packageName, R.layout.layout_custom_noti)
        remoteViews.setTextViewText(R.id.noti_tv_song_title, song.replace(".mp3", "").replace(".wav", ""))
        remoteViews.setImageViewResource(R.id.noti_bt_play_pause, R.drawable.baseline_pause_black_20)
        remoteViews.setImageViewResource(R.id.noti_bt_next_song, R.drawable.baseline_skip_next_black_20)
        remoteViews.setImageViewResource(R.id.noti_bt_previous_song, R.drawable.baseline_skip_previous_black_20)
        remoteViews.setImageViewResource(R.id.noti_bt_close, R.drawable.outline_close_black_20)

        if(isPlaying){
            remoteViews.setOnClickPendingIntent(R.id.noti_bt_play_pause, getPendingIntent(this, ACTION_PAUSE))
            remoteViews.setImageViewResource(R.id.noti_bt_play_pause, R.drawable.baseline_pause_black_20)
        }else{
            remoteViews.setOnClickPendingIntent(R.id.noti_bt_play_pause, getPendingIntent(this, ACTION_RESUME))
            remoteViews.setImageViewResource(R.id.noti_bt_play_pause, R.drawable.baseline_play_arrow_black_20)
        }
        remoteViews.setOnClickPendingIntent(R.id.noti_bt_next_song, getPendingIntent(this, ACTION_NEXT))
        remoteViews.setOnClickPendingIntent(R.id.noti_bt_previous_song, getPendingIntent(this, ACTION_PREVIOUS))
        remoteViews.setOnClickPendingIntent(R.id.noti_bt_close, getPendingIntent(this, ACTION_STOP))

        var notification: Notification = NotificationCompat.Builder(this, CHANEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
//            .setContentIntent(pendingIntent)
            .setCustomContentView(remoteViews)
            .build()

        startForeground(1, notification)
    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent{
        var intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("action_music", action)

        return PendingIntent.getBroadcast(context.applicationContext, action, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onDestroy() {
        super.onDestroy()
        isPlaying=false
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun sendActionToActivity(action: Int){
        var intent: Intent = Intent("send_action_to_activity")
//        var bundle = Bundle()
//        bundle.putString("song_uri", song_uri)
//        bundle.putBoolean("status_player", isPlaying)
//        bundle.putInt("action_music", action)
//        intent.putExtras(bundle)
//        intent.putExtra("song_uri", song_uri)
        intent.putExtra("time_of_music", mediaPlayer?.currentPosition)
        intent.putExtra("music_duration", mediaPlayer?.duration)
        intent.putExtra("list_song", listSong)
        intent.putExtra("position", position)
        intent.putExtra("status_player", isPlaying)
        intent.putExtra("action_music", action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}