package com.example.musicapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.adapter.AdapterViewPager
import com.example.musicapp.fragment.MusicAnimFragment
import com.example.musicapp.fragment.RelatedMusicFragment
import java.text.SimpleDateFormat

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

    lateinit var viewPager: ViewPager2
    var position: Int = 0

    lateinit var listSong1 : ArrayList<Song>



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
            listSong1 = p1.getSerializableExtra("list_song") as ArrayList<Song>
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
                sendActionToService(MyService.ACTION_NO_SHUFFLE)
            }else{
                btShuffle.setImageResource(R.drawable.ic_baseline_shuffle_true)
                isShuffle = true
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
        viewPager = findViewById(R.id.viewpager_song)

        var intent = intent
        var bundle: Bundle? = intent.extras
        if (bundle != null) {
            var how_to_start: String? = bundle.getString("how_to_start")
            if(how_to_start.equals("just_open")){
                position = bundle.getInt("position", 0)
                listSong1 = intent.getSerializableExtra("list_song1") as ArrayList<Song>
                tvSongName.setText(listSong1[position].title)
                duration = bundle.getInt("music_duration")
                isPlaying = bundle.getBoolean("status_player")

                updateTime()
                setStatusPlayOrPause()
            }else{
                position = bundle.getInt("position", 0)
                listSong1 = intent.getSerializableExtra("list_song1") as ArrayList<Song>
                tvSongName.setText(listSong1[position].title)
                stopService()
                startService()
            }
        }
        setUpViewPager()
    }

    fun startService(){
        var intent = Intent(this, MyService::class.java)
        intent.putExtra("position", position)

        //newcode
        intent.putExtra("list_song1", listSong1)
        intent.putExtra("song_uri", listSong1[position].resource)
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
        tvSongName.setText(listSong1[position].title)
        btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        updateTime()
        setUpViewPager()
    }

    private fun nextMusic(){
        tvSongName.setText(listSong1[position].title)
        btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        updateTime()
        setUpViewPager()
    }
    private fun previousMusic(){
        tvSongName.setText(listSong1[position].title)
        btPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        updateTime()
        setUpViewPager()
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
    fun setUpViewPager(){
        var listFrag : ArrayList<Fragment> = arrayListOf(
            MusicAnimFragment(),
            RelatedMusicFragment()
        )
        val adapter = AdapterViewPager(listFrag, this)
        viewPager.adapter = adapter
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

    override fun onBackPressed() {
        setResult(111)
        finish()
        super.onBackPressed()
    }
}