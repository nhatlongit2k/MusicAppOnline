package com.example.musicapp

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Song(
    var id: String?,
    var title: String,
    var artist: String?,
    var resource: String,
    var image: String?
    ) : Serializable {
    init {
        if(artist == null){
            artist = "unknown"
        }
    }
}