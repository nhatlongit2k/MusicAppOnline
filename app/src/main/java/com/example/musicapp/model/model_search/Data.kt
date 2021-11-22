package com.example.musicapp.model.model_search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data(
    @SerializedName("song")
    @Expose
    var listSong : ArrayList<Song>
    ) {

}