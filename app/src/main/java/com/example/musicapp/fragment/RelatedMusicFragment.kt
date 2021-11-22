package com.example.musicapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.PlayMusicActivity
import com.example.musicapp.R
import com.example.musicapp.Song
import com.example.musicapp.adapter.AdapterListSongOnl
import com.example.musicapp.adapter.AdapterListSongOnline
import com.example.musicapp.api.ApiMusic
import com.example.musicapp.model.model_related.RelatedData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RelatedMusicFragment : Fragment() {
    lateinit var playMusicActivity: PlayMusicActivity
    var listSong: ArrayList<Song> = ArrayList<Song>()
//    lateinit var songRecyclerView: RecyclerView
//    lateinit var layoutManager : LinearLayoutManager
    lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_related_music, container, false)
        playMusicActivity = activity as PlayMusicActivity
//        songRecyclerView = view.findViewById(R.id.recycler_song_list_related)
//        layoutManager = LinearLayoutManager(context)
//        songRecyclerView.layoutManager = layoutManager

        listView = view.findViewById(R.id.listview_fragRelated_song)

        getData()
        return view
    }

    private fun getData() {
        var id: String? = playMusicActivity.listSong1[playMusicActivity.position].id
        if(id != null){
            val retrofitBuilder = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
                .create(ApiMusic::class.java)
            val retrofitData = retrofitBuilder.getRelatedMusic("audio", id.toString())
            retrofitData.enqueue(object : Callback<RelatedData?> {
                override fun onResponse(
                    call: Call<RelatedData?>,
                    response: Response<RelatedData?>
                ) {
                    var dataSize = response.body()!!.data.item.size
                    for(i in 0..dataSize-1){
                        var resource = "http://api.mp3.zing.vn/api/streaming/audio/${response.body()!!.data.item[i].id}/128"
                        listSong?.add(Song(response.body()!!.data.item[i].id,
                            response.body()!!.data.item[i].title,
                            response.body()!!.data.item[i].artist,
                            resource,
                            response.body()!!.data.item[i].image))
                    }
//                    songRecyclerView.adapter = AdapterListSongOnl(activity, listSong!!)
                    listView.adapter = AdapterListSongOnline(activity, listSong!!)
                    listView.setOnItemClickListener { adapterView, view, i, l ->
                        playMusicActivity.setUpViewPager()
                        playMusicActivity.listSong1 = listSong
                        playMusicActivity.position = i
                        playMusicActivity.stopService()
                        playMusicActivity.startService()
                    }
                }

                override fun onFailure(call: Call<RelatedData?>, t: Throwable) {
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        }
    }
}