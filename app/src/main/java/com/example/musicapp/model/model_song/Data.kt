package com.example.musicapp.model.model_song

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data(
    @SerializedName("id")
    @Expose
    var id: String?,
    @SerializedName("title")
    @Expose
    var titles: String,
    @SerializedName("artists_names")
    @Expose
    var artist: String?,
    @SerializedName("thumbnail")
    @Expose
    var image: String?,
    @SerializedName("source")
    @Expose
    var source: Source?
) {

}