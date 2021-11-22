package com.example.musicapp.model.model_related

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SongRelated(
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
) {

}