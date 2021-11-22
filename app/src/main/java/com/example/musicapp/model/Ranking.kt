package com.example.musicapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Ranking(
    @SerializedName("err")
    @Expose
    var err: Int,
    @SerializedName("msg")
    @Expose
    var msg: String,
    @SerializedName("data")
    @Expose
    var dataArraySong: DataArraySong,
    @SerializedName("timestamp")
    @Expose
    var timestamp: Long
    ) {

}