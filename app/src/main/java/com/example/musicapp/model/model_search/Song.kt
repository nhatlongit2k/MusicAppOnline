package com.example.musicapp.model.model_search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Song(
    @SerializedName("id")
    @Expose
    var id: String?,
    @SerializedName("name")
    @Expose
    var title: String,
    @SerializedName("artist")
    @Expose
    var artist: String?,
    @SerializedName("thumb")
    @Expose
    var image: String?
) : Serializable {
    init {
        if(artist == null){
            artist = "unknown"
        }
    }
}