package com.example.musicapp.model.model_song

import com.example.musicapp.Song
import com.example.musicapp.model.DataArraySong
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SongData(
    @SerializedName("err")
    @Expose
    var err: Int,
    @SerializedName("msg")
    @Expose
    var msg: String,
    @SerializedName("data")
    @Expose
    var data: Data,
    @SerializedName("timestamp")
    @Expose
    var timestamp: Long
) {
}