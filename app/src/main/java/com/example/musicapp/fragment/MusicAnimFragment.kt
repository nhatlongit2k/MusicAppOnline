package com.example.musicapp.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.musicapp.PlayMusicActivity
import com.example.musicapp.R
import de.hdodenhof.circleimageview.CircleImageView

class MusicAnimFragment : Fragment() {

    lateinit var imgMusic: CircleImageView
    lateinit var playMusicActivity: PlayMusicActivity
    lateinit var animation: Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_music_anim, container, false)
        imgMusic = view.findViewById(R.id.circle_img_music)
        playMusicActivity = activity as PlayMusicActivity
        var stringImagePath: String? = playMusicActivity.listSong1[playMusicActivity.position].image
        if(stringImagePath?.contains("http") == true){
            var imgUrl = playMusicActivity.listSong1[playMusicActivity.position].image?.replace("w94_r1x1_jpeg/", "")
            DownloadImageFromInternet(imgMusic).execute(imgUrl)
        }else{
            if(getImageSongFromPath(stringImagePath!!)!=null){
                imgMusic.setImageBitmap(getImageSongFromPath(stringImagePath))
            }
            else
                imgMusic.setImageResource(R.drawable.daco)
        }
        animation = AnimationUtils.loadAnimation(context, R.anim.disc_rotate)
        imgMusic.startAnimation(animation)

        return view
    }

    private inner class DownloadImageFromInternet(var imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
        //        init {
//            Toast.makeText(context, "Please wait, it may take a few minute...",     Toast.LENGTH_SHORT).show()
//        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
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
}