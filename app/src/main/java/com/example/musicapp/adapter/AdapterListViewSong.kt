package com.example.musicapp.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.example.musicapp.R
import com.example.musicapp.Song

class AdapterListViewSong(val context: FragmentActivity?, val songList: ArrayList<Song>): BaseAdapter(){

    class ViewHolder(row: View){
        var imgSong: ImageView
        var tvTitle: TextView
        var tvAuthor: TextView

        init {
            imgSong = row.findViewById(R.id.img_item)
            tvTitle = row.findViewById(R.id.tv_title_song)
            tvAuthor = row.findViewById(R.id.tv_author_song)
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
        if(convertView == null){
            var layoutInflater: LayoutInflater = LayoutInflater.from(context)
            view = layoutInflater.inflate(R.layout.item_song, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = convertView.tag as ViewHolder
        }
        var song: Song = getItem(position) as Song
        viewHolder.tvTitle.text = song.title
        viewHolder.tvAuthor.text = song.artist
        if(getImageSongFromPath(song.image!!) == null){
            viewHolder.imgSong.setImageResource(R.drawable.music)
        }
        else{
            viewHolder.imgSong.setImageBitmap(getImageSongFromPath(song.image!!))
        }

        return view as View
    }

    fun getImageSongFromPath(path: String): Bitmap? {
//        val mmr = MediaMetadataRetriever()
//        mmr.setDataSource(path)
//        val albumImage = mmr.embeddedPicture
//        if (albumImage != null) {
//            return BitmapFactory.decodeByteArray(albumImage, 0, albumImage.size)
//        }
//        return null

        try {
            var retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            val art: ByteArray? = retriever.getEmbeddedPicture()
            var bitmap: Bitmap? = null
            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            }
            return bitmap
        }catch(e: Exception) {
            return null
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
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

}