package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.File
import java.text.SimpleDateFormat
import kotlin.concurrent.timer

class PlayMusicActivity : AppCompatActivity() {

    lateinit var btPlayOrPause :ImageButton
    lateinit var btPrevious :ImageButton
    lateinit var btNext :ImageButton
    lateinit var btShuffle :ImageButton
    lateinit var btRepeat :ImageButton
    var statusRepeat: Int = 0
    lateinit var tvSongName: TextView
    lateinit var tvTimeOfSong: TextView
    lateinit var tvTimeSongPlay: TextView
    lateinit var sbTimeSongPlay: SeekBar
    var position: Int = 0
    lateinit var listSong : ArrayList<File>
    var duration: Int = 0
    var currentTimeOfSong: Int = 0

    var isPlaying: Boolean? = false
    var isShuffle: Boolean? = false

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
//            var bundle: Bundle? = intent.extras
//            if(bundle == null){
//                return
//            }
//            var songUri = bundle?.get("song_uri")
//            isPlaying = bundle?.getBoolean("status_player")
//            var actionMusic = bundle?.getInt("action_music")
//            Log.d("TAG", "actionMusic: $actionMusic")
//            Log.d("TAG", "songUri: $songUri")
//            if (actionMusic != null) {
//                handleMusicFromService(actionMusic)
//            }
//            var songUri = p1?.getStringExtra("song_uri")
            position = p1!!.getIntExtra("position", 0)
            listSong = p1.getSerializableExtra("list_song") as ArrayList<File>
            isPlaying = p1.getBooleanExtra("status_player", false)
            var actionMusic = p1.getIntExtra("action_music", 0)
            duration = p1.getIntExtra("music_duration", 0)
            currentTimeOfSong = p1.getIntExtra("time_of_music", 0)
            if (actionMusic != null) {
                handleMusicFromService(actionMusic)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_music)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("send_action_to_activity"))

        initView()

//        if(mediaPlayer != null){
//            mediaPlayer.stop()
//            mediaPlayer.release()
//        }

        btNext.setOnClickListener {
            sendActionToService(MyService.ACTION_NEXT)
        }
        btPrevious.setOnClickListener {
            sendActionToService(MyService.ACTION_PREVIOUS)
        }
        btShuffle.setOnClickListener {
            if(isShuffle == true){
                btShuffle.setImageResource(R.drawable.ic_baseline_shuffle_24)
                isShuffle = false
                Log.d("TAG", "isShuffle: $isShuffle")
                sendActionToService(MyService.ACTION_NO_SHUFFLE)
            }else{
                btShuffle.setImageResource(R.drawable.ic_baseline_shuffle_true)
                isShuffle = true
                Log.d("TAG", "isShuffle: $isShuffle")
                sendActionToService(MyService.ACTION_SHUFFLE)
            }
        }
        btPlayOrPause.setOnClickListener {
            if(isPlaying == true){
                sendActionToService(MyService.ACTION_PAUSE)
            }else{
                sendActionToService(MyService.ACTION_RESUME)
            }
        }
        setRepeat()
        btRepeat.setOnClickListener {
            statusRepeat++
            setRepeat()
        }
        sbTimeSongPlay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                sendActionToService(MyService.ACTION_SEEK_BAR)
            }
        })
    }

    private fun initView() {
        btPlayOrPause = findViewById(R.id.bt_play_pause)
        btPrevious = findViewById(R.id.bt_previous_song)
        btNext = findViewById(R.id.bt_next_song)
        btShuffle = findViewById(R.id.bt_shuffle)
        btRepeat = findViewById(R.id.bt_repeat)
        tvSongName = findViewById(R.id.tv_song_name)
        tvTimeOfSong = findViewById(R.id.tv_time_of_song)
        tvTimeSongPlay = findViewById(R.id.tv_time_song_play)
        sbTimeSongPlay = findViewById(R.id.sb_time_song_play)

        var intent = intent
        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            listSong = intent.getSerializableExtra("listSong") as ArrayList<File>
            position = bundle.getInt("position", 0)
            tvSongName.setText(listSong[position].name.replace(".mp3", "").replace(".wav", ""))

//            var uri: Uri = Uri.parse(listSong[position].toString())
//            mediaPlayer = MediaPlayer.create(applicationContext, uri)
//            mediaPlayer.start()
            stopService()
            startService()
        }
    }

    fun startService(){
        //var song = Song(listSong[position].name.toString(), listSong[position].toString())
        var intent = Intent(this, MyService::class.java)
//        var bundle: Bundle = Bundle()
//        bundle.putString("song_uri", listSong[position].toString())
        intent.putExtra("list_song", listSong)
        intent.putExtra("song_uri", listSong[position].toString())
        intent.putExtra("position", position)
        startService(intent)
    }

    fun stopService(){
        var intent = Intent(this, MyService::class.java)
        stopService(intent)
    }
    private fun handleMusicFromService(action: Int){
        when(action){
            MyService.ACTION_START->{
                setStatusStartMusic()
            }
            MyService.ACTION_PAUSE->{
                setStatusPlayOrPause()
            }
            MyService.ACTION_RESUME->{
                setStatusPlayOrPause()
            }
            MyService.ACTION_NEXT->{
                nextMusic()
            }
            MyService.ACTION_PREVIOUS->{
                previousMusic()
            }
            MyService.ACTION_UPDATE_CURRENT_TIME->{
                updateCurTime()
            }
        }
    }

    private fun updateCurTime() {
        val timeFormat: SimpleDateFormat = SimpleDateFormat("mm:ss")
        tvTimeSongPlay.setText(timeFormat.format(currentTimeOfSong))
        sbTimeSongPlay.setProgress(currentTimeOfSong)
    }

    private fun setStatusStartMusic() {
        tvSongName.setText(listSong[position].name.replace(".mp3", "").replace(".wav", ""))
        btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        updateTime()
    }

    private fun nextMusic(){
        tvSongName.setText(listSong[position].name.replace(".mp3", "").replace(".wav", ""))
        btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        updateTime()
    }
    private fun previousMusic(){
        tvSongName.setText(listSong[position].name.replace(".mp3", "").replace(".wav", ""))
        btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        updateTime()
    }

    private fun updateTime(){
        val timeFormat: SimpleDateFormat = SimpleDateFormat("mm:ss")
        tvTimeOfSong.setText(timeFormat.format(duration))
        sbTimeSongPlay.max = duration
    }

    private fun sendActionToService(action: Int){
        var intent: Intent = Intent(this, MyService::class.java)
        intent.putExtra("action_music_service", action)
        if(action == MyService.ACTION_SEEK_BAR){
            intent.putExtra("time_of_seekbar", sbTimeSongPlay.progress)
        }
        startService(intent)
    }

    private fun setStatusPlayOrPause(){
        if(isPlaying == true){
            btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        }else{
            btPlayOrPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun setRepeat(){
        if(statusRepeat > 2){
            statusRepeat = 0
        }
        if(statusRepeat == 0){
            btRepeat.setImageResource(R.drawable.ic_baseline_repeat_24)
            sendActionToService(MyService.ACTION_NO_REPEAT)
        }
        if(statusRepeat == 1){
            btRepeat.setImageResource(R.drawable.ic_baseline_repeat_tre)
            sendActionToService(MyService.ACTION_REPEAT)
        }
        if(statusRepeat == 2){
            btRepeat.setImageResource(R.drawable.ic_baseline_repeat_one_tre)
            sendActionToService(MyService.ACTION_REPEAT_ONE_SONG)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}