package com.example.musicapp.model

import com.example.musicapp.Song
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataArraySong(
    @SerializedName("song")
    @Expose
    var songCode: ArrayList<SongCode>
) {
}