package com.example.musicapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Looper.getMainLooper
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var listView: ListView
    lateinit var item: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>
    val MY_PERMISSION_REQUEST = 1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            } else{
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
            }
        }
        else{
            doStuff()
        }
    }

    private fun doStuff() {
        listView = findViewById(R.id.listview_song)
        item = ArrayList<String>()
//        getMusic()
//        item = findSongs(Environment.getExternalStorageDirectory().toString())!!

        var itemSongs = findSongs(Environment.getExternalStorageDirectory())!!
        for(i in 0..itemSongs.size-1){
            item.add(itemSongs[i].name.toString())
        }



        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item)
        listView.adapter = adapter

        listView.setOnItemClickListener { adapterView, view, position, id ->
            var songName: String = listView.getItemAtPosition(position) as String
            var intent: Intent = Intent(applicationContext, PlayMusicActivity::class.java)
            intent.putExtra("listSong", itemSongs)
            intent.putExtra("SongName", songName)
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }


//    fun findSongs(rootPath: String?): ArrayList<String>? {
//        val fileList: ArrayList<String> = ArrayList()
//        try {
//            val rootFolder = File(rootPath)
//            val files: Array<File> =
//                rootFolder.listFiles() //here you will get NPE if directory doesn't contains  any file,handle it like this.
//            for (file in files) {
//                if (file.isDirectory()) {
//                    if (findSongs(file.getAbsolutePath()) != null) {
//                        fileList.addAll(findSongs(file.getAbsolutePath())!!)
//                    } else {
//                        break
//                    }
//                } else if (file.getName().endsWith(".mp3")) {
//                    fileList.add(file.getAbsolutePath())
//                }
//            }
//            return fileList
//        } catch (e: Exception) {
//            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//            return null
//        }
//    }

    fun findSongs(rootPath: File): ArrayList<File>? {
        val fileList: ArrayList<File> = ArrayList()
        try {
            val files: Array<File> =
                rootPath.listFiles() //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (file in files) {
                if (file.isDirectory()) {
                    if (findSongs(file) != null) {
                        fileList.addAll(findSongs(file)!!)
                    } else {
                        break
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    fileList.add(file)
                }
            }
            return fileList
        } catch (e: Exception) {
            //Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return null
        }
    }




    private fun getMusic(){
//        var contentResolver: ContentResolver = contentResolver
        var songUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        var songUri: Uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI

        Log.d("TAG", "getMusic uri: $songUri")
        var songCursor: Cursor? = contentResolver.query(songUri, null, null, null, null)

//        if(songCursor != null && songCursor.moveToFirst()){
//            var songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            var songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
//            do {
//                var currentTile: String = songCursor.getString(songTitle)
//                var currentArtist: String = songCursor.getString(songArtist)
//                item.add(currentTile + "\n"+ currentArtist)
//            }while (songCursor.moveToNext())
//        }else{
//            Toast.makeText(this, "Không có bài nhạc nào!", Toast.LENGTH_SHORT).show()
//        }

        if(songCursor == null){
            Toast.makeText(this, "Lỗi!", Toast.LENGTH_SHORT).show()
        }else if(!songCursor.moveToFirst()){
            Toast.makeText(this, "Không có bài nhạc nào!", Toast.LENGTH_SHORT).show()
        }else{
            var title = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            do {
                var st = songCursor.getString(title)
                item.add(st)
            }while (songCursor.moveToNext())
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSION_REQUEST -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                        doStuff()
                    }
                } else{
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}