package com.example.musicapp.model.model_related

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RelatedData(
    @SerializedName("err")
    @Expose
    var err: Int,
    @SerializedName("msg")
    @Expose
    var msg: String,
    @SerializedName("data")
    @Expose
    var data: Data
) {
}