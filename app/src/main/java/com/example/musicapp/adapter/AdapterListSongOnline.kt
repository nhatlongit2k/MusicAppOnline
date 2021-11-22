package com.example.musicapp.adapter

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.musicapp.Database
import com.example.musicapp.R
import com.example.musicapp.Song
import kotlin.collections.ArrayList

class AdapterListSongOnline(val context: FragmentActivity?, val songList: ArrayList<Song>): BaseAdapter(){

    var database: Database
    var listSongDatabase: ArrayList<Song> = ArrayList<Song>()

    init {
        database = Database(context, "DatabaseSongFavorite.sqlite", null, 1)
//        var cursor: Cursor = database.GetData("Select * FROM SONGFAVORITE")
//        while (cursor.moveToNext()){
//            val id = cursor.getString(0)
//            val title = cursor.getString(1)
//            val code = cursor.getString(2)
//            val artist = cursor.getString(3)
//            val resource = cursor.getString(4)
//            val image = cursor.getString(5)
//            listSongDatabase.add(Song(id, title, code, artist, resource, image))
//        }
        listSongDatabase = getFavoriteSongFromData()
    }


    class ViewHolder(row: View){
        var imgSong: ImageView
        var tvTitle: TextView
        var tvAuthor: TextView
        var imgFavorite: ImageView
        var imgDownload: ImageView

        init {
            imgSong = row.findViewById(R.id.img_item_song_online)
            tvTitle = row.findViewById(R.id.tv_title_song_online)
            tvAuthor = row.findViewById(R.id.tv_author_song_online)
            imgFavorite = row.findViewById(R.id.img_favorite_song_online)
            imgDownload = row.findViewById(R.id.img_download_song_online)
        }
    }

    override fun getCount(): Int {
        return songList.size
    }

    override fun getItem(position: Int): Any {
        return songList.get(position)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, p2: ViewGroup?): View {
        var view : View?
        var viewHolder: ViewHolder

//        getFavoriteSongFromData()


        if(convertView == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.item_song_online, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        var song: Song = getItem(position) as Song
        viewHolder.tvTitle.text = song.title
        viewHolder.tvAuthor.text = song.artist
        DownloadImageFromInternet(viewHolder.imgSong).execute(song.image)
        if(isInFavorite(song)){
            viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
        }else{
            viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        viewHolder.imgFavorite.setOnClickListener {
            if(isInFavorite(song)){
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                database.QueryData("DELETE FROM SONGFAVORITE WHERE ID = '${song.id}'")
                listSongDatabase = getFavoriteSongFromData()
            }else{
                viewHolder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                database.QueryData("INSERT INTO SONGFAVORITE VALUES('"+song.id+"', '${song.title}', '${song.artist}', '${song.resource}', '${song.image}')")
                listSongDatabase = getFavoriteSongFromData()
            }
        }

        viewHolder.imgDownload.setOnClickListener {
            stratDownloadFile(song)
        }

        return view as View
    }

    private fun stratDownloadFile(song: Song) {
        Log.d("TAG", "stratDownloadFile: ${song.resource}")
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(song.resource))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(song.title)
        request.setDescription("Download song...")

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, System.currentTimeMillis().toString())

        var downloadManager: DownloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        if(downloadManager!=null){
            downloadManager.enqueue(request)
        }
    }

    fun isInFavorite(song: Song): Boolean {
        for(i in 0..listSongDatabase.size-1){
            if(listSongDatabase.get(i).id.equals(song.id)==true){
                return true
            }
        }
        return false
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




    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        //        init {
//            Toast.makeText(context, "Please wait, it may take a few minute...",     Toast.LENGTH_SHORT).show()
//        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

}