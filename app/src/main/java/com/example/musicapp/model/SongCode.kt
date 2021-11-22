package com.example.musicapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SongCode(
    @SerializedName("id")
    @Expose
    var id: String?,
    @SerializedName("title")
    @Expose
    var title: String,
    @SerializedName("artists_names")
    @Expose
    var artist: String?,
    @SerializedName("thumbnail")
    @Expose
    var image: String?



//    @SerializedName("code")
//    @Expose
//    var code: String
) {
}