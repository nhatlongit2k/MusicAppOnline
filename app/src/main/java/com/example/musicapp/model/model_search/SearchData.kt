package com.example.musicapp.model.model_search

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SearchData(
    @SerializedName("result")
    @Expose
    var result: Boolean,
    @SerializedName("data")
    @Expose
    var data: ArrayList<Data>
) {
}