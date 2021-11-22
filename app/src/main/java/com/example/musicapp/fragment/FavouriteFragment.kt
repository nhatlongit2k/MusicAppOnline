package com.example.musicapp.fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.musicapp.*
import com.example.musicapp.adapter.AdapterListSongOnline

class FavouriteFragment : Fragment() {

    var listSong: ArrayList<Song>? = ArrayList<Song>()
    lateinit var database: Database
    lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        listView = view.findViewById(R.id.listview_fragFavorite_song)
        database = Database(context, "DatabaseSongFavorite.sqlite", null, 1)
        listSong = getFavoriteSongFromData()
        listView.adapter = AdapterListSongOnline(activity, listSong!!)

        listView.setOnItemClickListener { adapterView, view, position, id ->
            var intent: Intent = Intent(activity, PlayMusicActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("list_song1", listSong)
            intent.putExtra("song_name1", listSong!![position].title)
//            resultLaucher.launch(intent)
//            startActivity(intent)
            startActivityForResult(intent, 1)
        }
        return view
    }

    fun getFavoriteSongFromData(): ArrayList<Song> {
        var listSong = ArrayList<Song>()
        var cursor: Cursor = database.GetData("Select * FROM SONGFAVORITE")
        while (cursor.moveToNext()){
            val id = cursor.getString(0)
            val title = cursor.getString(1)
            val artist = cursor.getString(2)
            val resource = cursor.getString(3)
            val image = cursor.getString(4)
            listSong.add(Song(id, title, artist, resource, image))
        }
        return listSong
    }
}