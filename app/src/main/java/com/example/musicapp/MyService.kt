package com.example.musicapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.musicapp.MyApplication.Companion.CHANEL_ID
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class MyService : Service() {

    companion object{
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
        val ACTION_STOP: Int = 12
        val ACTION_UPDATE_CURRENT_TIME = 99

        val ACTION_GET_MUSIC_FOR_MAIN: Int = 100
        val ACTION_NEXT_MUSIC_FROM_MAIN: Int = 101
        val ACTION_PREVIOUS_MUSIC_FROM_MAIN: Int = 102
        val ACTION_RESUME_MUSIC_FROM_MAIN: Int = 103
        val ACTION_PAUSE_MUSIC_FROM_MAIN: Int = 104

        val ACTION_OPEN_PLAY_ACTIVITY_FROM_MAIN = 999
    }
    var isPlaying: Boolean = false
    var mediaPlayer: MediaPlayer? = null
    var listSong1: ArrayList<Song> = ArrayList<Song>()
    var coppyOfListSong1 : ArrayList<Song> = ArrayList<Song>()
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
            var get_song_uri: String? = intent?.getStringExtra("song_uri")
            if(get_song_uri != null){
                position = bundle.getInt("position", 0)
                song_uri = get_song_uri
                listSong1 = intent?.getSerializableExtra("list_song1") as ArrayList<Song>
                coppyOfListSong1.addAll(listSong1)
                startMusic(song_uri)
                sendNotification(listSong1[position])
            }
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
        return START_NOT_STICKY
    }


    private fun startMusic(song: String) {
        var uri: Uri = Uri.parse(song)
//        var uri: Uri = Uri.parse("http://api.mp3.zing.vn/api/streaming/audio/ZW8I7AAI/128")
//        var uri: Uri = Uri.parse("https://mp3-s1-m-zmp3.zadn.vn/ffab9ce685a76cf935b6/2885442504735776385?authen=exp=1637471274~acl=/ffab9ce685a76cf935b6/*~hmac=6735c0bc7df7f71b8637b368b4336fca&fs=MTYzNzI5ODQ3NDk1N3x3ZWJWNHwxLjU0LjE5OS4yNDmUsIC")
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
                sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
            }
            ACTION_RESUME->{
                resumeMusic()
                sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
            }
            ACTION_NEXT->{
                nextMusic()
                sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
            }
            ACTION_PREVIOUS->{
                previousMusic()
                sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
            }
            ACTION_SEEK_BAR->{
                setTimeSong()
            }
            ACTION_NO_REPEAT->{
                mediaPlayer?.setOnCompletionListener {
                    position ++
                    if(position>=listSong1.size-1 ){
                        stopSelf()
                    }else{
                        var uri: Uri = Uri.parse(listSong1[position].resource)
                        mediaPlayer?.stop()
                        mediaPlayer = null
                        mediaPlayer = MediaPlayer.create(applicationContext, uri)
                        mediaPlayer?.start()
                        isPlaying = true
                        sendNotification(listSong1[position])
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
                    var uri: Uri = Uri.parse(listSong1[position].resource)
                    mediaPlayer?.stop()
                    mediaPlayer = null
                    mediaPlayer = MediaPlayer.create(applicationContext, uri)
                    mediaPlayer?.start()
                    isPlaying = true
                    sendNotification(listSong1[position])
                    sendActionToActivity(ACTION_NEXT)
                }
            }
            ACTION_SHUFFLE ->{
                var file = listSong1[position]
                Collections.shuffle(listSong1)
                Collections.swap(listSong1, 0, listSong1.indexOf(file))
                position = 0
            }
            ACTION_NO_SHUFFLE ->{
                var file = listSong1[position]
                listSong1.clear()
                listSong1.addAll(coppyOfListSong1)
                position = listSong1.indexOf(file)
            }
            ACTION_GET_MUSIC_FOR_MAIN->{
                sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
            }
            ACTION_NEXT_MUSIC_FROM_MAIN->{
                if(position >= listSong1.size - 1){
                    position = 0
                }else{
                    position++
                }
                var uri: Uri = Uri.parse(listSong1[position].resource)
                mediaPlayer?.stop()
                mediaPlayer = null
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                mediaPlayer?.start()
                isPlaying = true
                sendNotification(listSong1[position])
                sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
            }
            ACTION_PREVIOUS_MUSIC_FROM_MAIN->{
                if(position <= 0){
                    position = listSong1.size-1
                }else{
                    position--
                }
                var uri: Uri = Uri.parse(listSong1[position].resource)
                mediaPlayer?.stop()
                mediaPlayer = null
                mediaPlayer = MediaPlayer.create(applicationContext, uri)
                mediaPlayer?.start()
                isPlaying = true
                sendNotification(listSong1[position])
                sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
            }
            ACTION_RESUME_MUSIC_FROM_MAIN->{
                if(mediaPlayer!=null && !isPlaying){
                    mediaPlayer!!.start()
                    isPlaying = true
                    sendNotification(listSong1[position])
                    sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
                }
            }
            ACTION_PAUSE_MUSIC_FROM_MAIN->{
                if(mediaPlayer != null && isPlaying){
                    if(mediaPlayer != null && isPlaying){
                        mediaPlayer!!.pause()
                        isPlaying = false
                        sendNotification(listSong1[position])
                        sendActionToMainActivity(ACTION_GET_MUSIC_FOR_MAIN)
                    }
                }
            }
            ACTION_OPEN_PLAY_ACTIVITY_FROM_MAIN->{
                sendActionToMainActivity(ACTION_OPEN_PLAY_ACTIVITY_FROM_MAIN)
            }
        }
    }

    private fun setTimeSong() {
        mediaPlayer?.seekTo(timeOfSeekBar!!)
    }


    private fun previousMusic() {
        if(position <= 0){
            position = listSong1.size-1
        }else{
            position--
        }
        var uri: Uri = Uri.parse(listSong1[position].resource)
        mediaPlayer?.stop()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(applicationContext, uri)
        mediaPlayer?.start()
        isPlaying = true
        sendNotification(listSong1[position])
        sendActionToActivity(ACTION_PREVIOUS)
    }

    private fun nextMusic() {
        if(position >= listSong1.size - 1){
            position = 0
        }else{
            position++
        }
        var uri: Uri = Uri.parse(listSong1[position].resource)
        mediaPlayer?.stop()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(applicationContext, uri)
        mediaPlayer?.start()
        isPlaying = true
        sendNotification(listSong1[position])
        sendActionToActivity(ACTION_NEXT)
    }

    private fun resumeMusic() {
        if(mediaPlayer!=null && !isPlaying){
            mediaPlayer!!.start()
            isPlaying = true
            sendNotification(listSong1[position])
            sendActionToActivity(ACTION_RESUME)
        }
    }

    private fun pauseMusic(){
        if(mediaPlayer != null && isPlaying){
            mediaPlayer!!.pause()
            isPlaying = false
            sendNotification(listSong1[position])
            sendActionToActivity(ACTION_PAUSE)
        }
    }

    private fun sendNotification(song: Song) {
        var remoteViews = RemoteViews(packageName, R.layout.layout_custom_noti)
        remoteViews.setTextViewText(R.id.noti_tv_song_title, song.title)
        remoteViews.setTextViewText(R.id.noti_tv_song_author, song.artist)
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

        var intent: Intent = Intent(this, PlayMusicActivity::class.java)
        intent.putExtra("how_to_start", "just_open")
        intent.putExtra("position", position)
        intent.putExtra("list_song1", listSong1)
        intent.putExtra("isplaying", isPlaying)
        intent.putExtra("music_duration", mediaPlayer?.duration)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        var notification: Notification = NotificationCompat.Builder(this, CHANEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_music_note_24)
            .setContentIntent(pendingIntent)
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
        var intent = Intent("send_action_to_activity")
        intent.putExtra("time_of_music", mediaPlayer?.currentPosition)
        intent.putExtra("music_duration", mediaPlayer?.duration)
        intent.putExtra("list_song", listSong1)
        intent.putExtra("position", position)
        intent.putExtra("status_player", isPlaying)
        intent.putExtra("action_music", action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    private fun sendActionToMainActivity(action: Int){
        var intent = Intent("send_action_to_main_activity")
        intent.putExtra("song_name", listSong1[position])
        intent.putExtra("list_song", listSong1)
        intent.putExtra("music_duration", mediaPlayer?.duration)
        intent.putExtra("position", position)
        intent.putExtra("status_player", isPlaying)
        intent.putExtra("action_music", action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}