package com.example.musicapp.model.model_song

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Source(
    @SerializedName("128")
    @Expose
    var lowMusic: String,
    @SerializedName("320")
    @Expose
    var hightMusic: String?
) {
}