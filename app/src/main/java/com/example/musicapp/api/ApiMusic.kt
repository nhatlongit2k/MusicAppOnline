package com.example.musicapp.api

import com.example.musicapp.model.Ranking
import com.example.musicapp.model.model_related.RelatedData
import com.example.musicapp.model.model_search.SearchData
import com.example.musicapp.model.model_song.SongData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiMusic {

    //https://mp3.zing.vn/xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1

    @Headers("Content-type: application/json")
    @GET("xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1")
    fun getData(): Call<Ranking>



    //xhr/media/get-source?type=audio&key=kmJHTZHNCVaSmSuymyFHLH
    @Headers("Content-type: application/json")
    @GET("xhr/media/get-source")
    fun getMusicSource(@Query("type") type: String, @Query("key") Key: String): Call<SongData>

    @Headers("Content-type: application/json")
    @GET("complete")
    fun getMusicSearch(@Query("type") type: String,
                       @Query("num") num: String,
                       @Query("query") query: String): Call<SearchData>


    @Headers("Content-type: application/json")
    @GET("xhr/recommend")
    fun getRelatedMusic(@Query("type") type: String, @Query("id") id:String): Call<RelatedData>
}