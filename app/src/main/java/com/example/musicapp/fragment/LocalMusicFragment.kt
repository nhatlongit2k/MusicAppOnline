package com.example.musicapp.fragment

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.example.musicapp.adapter.AdapterListViewSong
import com.example.musicapp.PlayMusicActivity
import com.example.musicapp.R
import com.example.musicapp.Song
import android.media.MediaMetadataRetriever
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.util.Log

class LocalMusicFragment : Fragment() {
    lateinit var listView: ListView
    var listSong: ArrayList<Song> = ArrayList<Song>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_local_music, container, false)
        listView = view.findViewById(R.id.listview_song)
        getMusic(view)
        listView.adapter = AdapterListViewSong(activity, listSong)
        listView.setOnItemClickListener { adapterView, view, position, id ->
            var intent: Intent = Intent(activity, PlayMusicActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("list_song1", listSong)
            intent.putExtra("song_name1", listSong[position].title)
//            resultLaucher.launch(intent)
//            startActivity(intent)
            startActivityForResult(intent, 1)
        }


        return view
    }

    private fun getMusic(view: View) {

        var songUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var selection: String = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        var songCursor: Cursor? = activity?.contentResolver?.query(songUri, null, selection, null, null)

        if(songCursor == null){
            Toast.makeText(activity, "Lỗi!", Toast.LENGTH_SHORT).show()
        }else if(!songCursor.moveToFirst()){
            Toast.makeText(activity, "Không có bài nhạc nào!", Toast.LENGTH_SHORT).show()
        }else{
            var id = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            var title = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            var artist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            var resource = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            do {
                listSong.add(Song(songCursor.getString(id), songCursor.getString(title), songCursor.getString(artist), songCursor.getString(resource), songCursor.getString(resource)))
//                listSong.add(Song(songCursor.getString(title), songCursor.getString(artist), songCursor.getString(resource)))
            }while (songCursor.moveToNext())
        }
    }

    fun getImageSongFromPath(path: String): Bitmap? {
//        val mmr = MediaMetadataRetriever()
//        mmr.setDataSource(path)
//        val albumImage = mmr.embeddedPicture
//        if (albumImage != null) {
//            return BitmapFactory.decodeByteArray(albumImage, 0, albumImage.size)
//        }
//        return null

        var retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val art: ByteArray? = retriever.getEmbeddedPicture()
        var bitmap: Bitmap? = null
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
        }
        return bitmap
    }
}