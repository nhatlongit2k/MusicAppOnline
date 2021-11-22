package com.example.musicapp

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Looper.getMainLooper
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.File
import java.lang.Exception
import android.app.ActivityManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.opengl.Visibility
import android.os.*
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var listView: ListView
    lateinit var listSong: ArrayList<Song>
    lateinit var rlBottom: RelativeLayout
    lateinit var tvSongName: TextView
    lateinit var tvArtist: TextView
    lateinit var imgPreviousSong: ImageView
    lateinit var imgNextSong: ImageView
    lateinit var imgPlayOrPause: ImageView
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var database: Database
    lateinit var btSearch: ImageView
    lateinit var edtSearch: EditText

    var isPlaying : Boolean?= false

    companion object{
        val MY_PERMISSION_REQUEST = 1
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            var song: Song = p1?.getSerializableExtra("song_name") as Song
            var listSongPlaying: ArrayList<Song> = p1?.getSerializableExtra("list_song") as ArrayList<Song>
            var position: Int = p1.getIntExtra("position", 0)
            isPlaying = p1?.getBooleanExtra("status_player", false)
            var duration: Int = p1.getIntExtra("music_duration", 0)
            var action = p1?.getIntExtra("action_music", 0)
            if(action == MyService.ACTION_GET_MUSIC_FOR_MAIN){
                rlBottom.visibility = View.VISIBLE
                tvSongName.setText(listSongPlaying[position].title)
                tvArtist.setText(listSongPlaying[position].artist)
                if(isPlaying == true){
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
                }else{
                    imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
            if(action == MyService.ACTION_OPEN_PLAY_ACTIVITY_FROM_MAIN){
                var intent: Intent = Intent(this@MainActivity, PlayMusicActivity::class.java)
                intent.putExtra("how_to_start", "just_open")
                intent.putExtra("position", position)
                intent.putExtra("list_song1", listSongPlaying)
                intent.putExtra("music_duration", duration)
                intent.putExtra("status_player", isPlaying)
                Log.d("TAG", "day: ")
                startActivityForResult(intent, 1)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Database(this, "DatabaseSongFavorite.sqlite", null, 1)
        database.QueryData("CREATE TABLE IF NOT EXISTS SONGFAVORITE(ID VARCHAR(100) PRIMARY KEY , Title VARCHAR(100), Artist VARCHAR(100), Resource VARCHAR(100), Image VARCHAR(100))")
        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            } else{
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            }
        }
        else{

            initView()

            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("send_action_to_main_activity"))
            if(isMyServiceRunning(MyService::class.java)){
                sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
            }
        }


    }

    private fun initView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)

        rlBottom = findViewById(R.id.main_layout_bottom)
        tvSongName = findViewById(R.id.main_tv_song_title)
        tvArtist = findViewById(R.id.main_tv_song_author)
        imgNextSong = findViewById(R.id.main_bt_next_song)
        imgPreviousSong = findViewById(R.id.main_bt_previous_song)
        imgPlayOrPause = findViewById(R.id.main_bt_play_pause)
        btSearch = findViewById(R.id.bt_find_song)
        edtSearch = findViewById(R.id.edt_find_song)

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
        btSearch.setOnClickListener {
            var intent: Intent = Intent(this, SearchMusicActivity::class.java)
            intent.putExtra("edt_search", edtSearch.text.toString())
            startActivity(intent)
        }

        rlBottom.setOnClickListener {
            sendActionToService(MyService.ACTION_OPEN_PLAY_ACTIVITY_FROM_MAIN)
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

//    private fun doStuff() {
//        listView = findViewById(R.id.listview_song)
//
//        listSong = ArrayList<Song>()
//
//        rlBottom = findViewById(R.id.main_layout_bottom)
//        tvSongName = findViewById(R.id.main_tv_song_title)
//        tvAuthor = findViewById(R.id.main_tv_song_author)
//        imgNextSong = findViewById(R.id.main_bt_next_song)
//        imgPreviousSong = findViewById(R.id.main_bt_previous_song)
//        imgPlayOrPause = findViewById(R.id.main_bt_play_pause)
//        getMusic()
//        listView.adapter = AdapterListViewSong(this@MainActivity, listSong)
//
//        val resultLaucher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
//            if(result.resultCode == 111){
//                sendActionToService(MyService.ACTION_GET_MUSIC_FOR_MAIN)
//            }
//        }
//        listView.setOnItemClickListener { adapterView, view, position, id ->
//
//            var intent: Intent = Intent(applicationContext, PlayMusicActivity::class.java)
//            intent.putExtra("position", position)
//            intent.putExtra("list_song1", listSong)
//            intent.putExtra("song_name1", listSong[position].title)
//
//            resultLaucher.launch(intent)
////            startActivity(intent)
//
//        }
//
//        imgNextSong.setOnClickListener {
//            sendActionToService(MyService.ACTION_NEXT_MUSIC_FROM_MAIN)
//        }
//        imgPreviousSong.setOnClickListener {
//            sendActionToService(MyService.ACTION_PREVIOUS_MUSIC_FROM_MAIN)
//        }
//        imgPlayOrPause.setOnClickListener {
//            if(isPlaying == true){
//                sendActionToService(MyService.ACTION_PAUSE_MUSIC_FROM_MAIN)
//            }else{
//                sendActionToService(MyService.ACTION_RESUME_MUSIC_FROM_MAIN)
//            }
//        }
//    }




//    private fun getMusic(){
////        var contentResolver: ContentResolver = contentResolver
//        var songUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        var selection: String = MediaStore.Audio.Media.IS_MUSIC + "!=0"
////        var songUri: Uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
//
//        var songCursor: Cursor? = contentResolver.query(songUri, null, selection, null, null)
//
////        if(songCursor != null && songCursor.moveToFirst()){
////            var songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
////            var songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
////            do {
////                var currentTile: String = songCursor.getString(songTitle)
////                var currentArtist: String = songCursor.getString(songArtist)
////                item.add(currentTile + "\n"+ currentArtist)
////            }while (songCursor.moveToNext())
////        }else{
////            Toast.makeText(this, "Không có bài nhạc nào!", Toast.LENGTH_SHORT).show()
////        }
//
//        if(songCursor == null){
//            Toast.makeText(this, "Lỗi!", Toast.LENGTH_SHORT).show()
//        }else if(!songCursor.moveToFirst()){
//            Toast.makeText(this, "Không có bài nhạc nào!", Toast.LENGTH_SHORT).show()
//        }else{
//            var title = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            var artist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
//            var resource = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
//            do {
//                listSong.add(Song(songCursor.getString(title), songCursor.getString(artist), songCursor.getString(resource), getImageSongFromPath(songCursor.getString(resource))))
//            }while (songCursor.moveToNext())
//        }
//    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSION_REQUEST -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
//                        doStuff()
                        initView()
                    }
                } else{
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun sendActionToService(action: Int){
        var intent: Intent = Intent(this, MyService::class.java)
        intent.putExtra("action_music_service", action)
        startService(intent)
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