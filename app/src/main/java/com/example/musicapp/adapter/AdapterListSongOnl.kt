package com.example.musicapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
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
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.Database
import com.example.musicapp.PlayMusicActivity
import com.example.musicapp.R
import com.example.musicapp.Song

class AdapterListSongOnl(val context: Activity?, val listSong: ArrayList<Song>): RecyclerView.Adapter<AdapterListSongOnl.MyViewHolder>() {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_song_online, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = listSong[position]
        holder.tvTitle.text = currentItem.title
        holder.tvAuthor.text = currentItem.artist
        DownloadImageFromInternet(holder.imgSong).execute(currentItem.image)
        if(isInFavorite(currentItem)){
            holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
        }else{
            holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        holder.imgFavorite.setOnClickListener {
            if(isInFavorite(currentItem)){
                holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                database.QueryData("DELETE FROM SONGFAVORITE WHERE ID = '${currentItem.id}'")
                listSongDatabase = getFavoriteSongFromData()
            }else{
                holder.imgFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                database.QueryData("INSERT INTO SONGFAVORITE VALUES('"+currentItem.id+"', '${currentItem.title}', '${currentItem.artist}', '${currentItem.resource}', '${currentItem.image}')")
                listSongDatabase = getFavoriteSongFromData()
            }
        }
        holder.imgDownload.setOnClickListener {
            stratDownloadFile(currentItem)
        }
        holder.itemView.setOnClickListener {
            var intent: Intent = Intent(context, PlayMusicActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("list_song1", listSong)

//            resultLaucher.launch(intent)
//            startActivity(intent)
            context?.startActivityForResult(intent, 1)
        }
    }

    override fun getItemCount(): Int {
        return listSong.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var imgSong: ImageView = itemView.findViewById(R.id.img_item_song_online)
        var tvTitle: TextView = itemView.findViewById(R.id.tv_title_song_online)
        var tvAuthor: TextView = itemView.findViewById(R.id.tv_author_song_online)
        var imgFavorite: ImageView = itemView.findViewById(R.id.img_favorite_song_online)
        var imgDownload: ImageView = itemView.findViewById(R.id.img_download_song_online)
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
                return null
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            if(result!=null){
                imageView.setImageBitmap(result)
            }else{
                imageView.setImageResource(R.drawable.music)
            }
        }
    }
}