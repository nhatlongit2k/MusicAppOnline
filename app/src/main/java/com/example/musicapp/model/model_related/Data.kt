package com.example.musicapp.model.model_related

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data(
    @SerializedName("items")
    @Expose
    var item: ArrayList<SongRelated>,
    @SerializedName("total")
    @Expose
    var total: Int
) {
}