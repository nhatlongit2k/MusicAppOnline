package com.example.musicapp

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.adapter.AdapterListSongOnl
import com.example.musicapp.api.ApiMusic
import com.example.musicapp.model.model_search.Data
import com.example.musicapp.model.model_search.SearchData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val SEARCH_URL = "http://ac.mp3.zing.vn/"
class SearchMusicActivity : AppCompatActivity() {

    var listSong: ArrayList<Song> = ArrayList()

    lateinit var tvSongName: TextView
    lateinit var tvArtist: TextView
    lateinit var rlBottom: RelativeLayout
    lateinit var imgPreviousSong: ImageView
    lateinit var imgNextSong: ImageView
    lateinit var imgPlayOrPause: ImageView
    lateinit var imgBack: ImageView
    var isPlaying : Boolean?= false

    lateinit var data: Data
    lateinit var songRecyclerView: RecyclerView
    lateinit var layoutManager : LinearLayoutManager

    lateinit var pbLoading: ProgressBar

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            var song: Song = p1?.getSerializableExtra("song_name") as Song
            isPlaying = p1?.getBooleanExtra("status_player", false)
            var action = p1?.getIntExtra("action_music", 0)
            if(action == MyService.ACTION_GET_MUSIC_FOR_MAIN){
                rlBottom.visibility = View.VISIBLE
                tvSongName.setText(song.title)
                tvArtist.setText(song.artist)
                if(isPlaying == true){
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
                }else{
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_music)

        initView()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("send_action_to_main_activity"))
        if(isMyServiceRunning(MyService::class.java)){
            sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
        }

        songRecyclerView = findViewById(R.id.recycler_song_list_search)
        layoutManager = LinearLayoutManager(this)
        songRecyclerView.layoutManager = layoutManager

        pbLoading = findViewById(R.id.pb_search_loading)
        pbLoading.visibility = View.VISIBLE
        var searchString = intent.getStringExtra("edt_search")
        if(searchString!=null){
            getData(searchString)
        }
    }

    private fun initView() {
        tvSongName = findViewById(R.id.search_tv_song_title)
        tvArtist = findViewById(R.id.search_tv_song_author)
        imgNextSong = findViewById(R.id.search_bt_next_song)
        imgPreviousSong = findViewById(R.id.search_bt_previous_song)
        imgPlayOrPause = findViewById(R.id.search_bt_play_pause)
        rlBottom = findViewById(R.id.search_layout_bottom)
        imgBack = findViewById(R.id.bt_Search_back)

        imgNextSong.setOnClickListener {
            sendActionToService(MyService.ACTION_NEXT_MUSIC_FROM_MAIN)
        }
        imgPreviousSong.setOnClickListener {
            sendActionToService(MyService.ACTION_PREVIOUS_MUSIC_FROM_MAIN)
        }
        imgPlayOrPause.setOnClickListener {
            if(isPlaying == true){
                sendActionToService(MyService.ACTION_PAUSE_MUSIC_FROM_MAIN)
            }else{
                sendActionToService(MyService.ACTION_RESUME_MUSIC_FROM_MAIN)
            }
        }
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendActionToService(action: Int){
        var intent: Intent = Intent(this, MyService::class.java)
        intent.putExtra("action_music_service", action)
        startService(intent)
    }

    private fun getData(searchString: String?) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(SEARCH_URL)
            .build()
            .create(ApiMusic::class.java)

        val retrofitData = retrofitBuilder.getMusicSearch("artist,song,key,code", "500", searchString!!)
        retrofitData.enqueue(object : Callback<SearchData?> {
            override fun onResponse(call: Call<SearchData?>, response: Response<SearchData?>) {
                Log.d("TAG", "onResponse: Server response: ${response.toString()}")
                Log.d("TAG", "recerved infomation: ${response.body().toString()}")
                if(response.body()==null){

                }else{
                    data = response.body()?.data!![0]
                    for(i in 0..data.listSong.size-1){
                        val resource: String = "http://api.mp3.zing.vn/api/streaming/audio/${data.listSong[i].id}/128"
                        val image: String = "https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/${data.listSong[i].image}"
                        listSong.add(Song(data.listSong[i].id, data.listSong[i].title, data.listSong[i].artist, resource, image))
                    }
                    pbLoading.visibility = View.GONE
                    songRecyclerView.adapter = AdapterListSongOnl(this@SearchMusicActivity, listSong!!)
                }
            }

            override fun onFailure(call: Call<SearchData?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == 111){
            if(isMyServiceRunning(MyService::class.java)){
                sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}