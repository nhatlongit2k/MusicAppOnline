package com.example.musicapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.*
import com.example.musicapp.adapter.AdapterListSongOnl
import com.example.musicapp.api.ApiMusic
import com.example.musicapp.model.Ranking
import com.example.musicapp.model.model_song.SongData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://mp3.zing.vn/"
class RankingFragment : Fragment() {

    var listSong: ArrayList<Song>? = ArrayList<Song>()
    lateinit var rankingList: Ranking
    lateinit var pbLoading: ProgressBar
//    lateinit var listView: ListView
    lateinit var songRecyclerView: RecyclerView
    lateinit var layoutManager : LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_ranking, container, false)

//        listView = view.findViewById(R.id.listview_fragRanking_song)
        songRecyclerView = view.findViewById(R.id.recycler_song_list)
        layoutManager = LinearLayoutManager(context)
        songRecyclerView.layoutManager = layoutManager
        pbLoading = view.findViewById(R.id.pb_loading)
        pbLoading.visibility = View.VISIBLE
        getData()

        return view
    }

    private fun getData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiMusic::class.java)

        val retrofitData = retrofitBuilder.getData()

        retrofitData.enqueue(object : Callback<Ranking?> {
            override fun onResponse(call: Call<Ranking?>, response: Response<Ranking?>) {
                Log.d("TAG", "onResponse: Server response: ${response.toString()}")
                Log.d("TAG", "recerved infomation: ${response.body().toString()}")

                rankingList = response.body()!!
                var dataList = response.body()!!.dataArraySong.songCode.size
                for(i in 0..dataList-1){
//                    getSongData(response.body()?.dataArraySong?.songCode?.get(i)?.code.toString())
                    var resource = "http://api.mp3.zing.vn/api/streaming/audio/${response.body()!!.dataArraySong?.songCode.get(i).id}/128"
                    listSong?.add(Song(response.body()!!.dataArraySong?.songCode.get(i).id,
                        response.body()!!.dataArraySong?.songCode.get(i).title,
                        response.body()!!.dataArraySong?.songCode.get(i).artist,
                        resource,
                        response.body()!!.dataArraySong?.songCode.get(i).image))
                }
//                listView.adapter = AdapterListSongOnline(activity, listSong!!)
//
//                listView.setOnItemClickListener { adapterView, view, position, id ->
//                    var intent: Intent = Intent(activity, PlayMusicActivity::class.java)
//                    intent.putExtra("position", position)
//                    intent.putExtra("list_song1", listSong)
//                    intent.putExtra("song_name1", listSong!![position].title)
////            resultLaucher.launch(intent)
////            startActivity(intent)
//                    startActivityForResult(intent, 1)
//                }


                pbLoading.visibility = View.GONE
                songRecyclerView.adapter = AdapterListSongOnl(activity, listSong!!)
            }

            override fun onFailure(call: Call<Ranking?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    }

    private fun getSongData(key: String){
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiMusic::class.java)
        val retrofitData = retrofitBuilder.getMusicSource("audio", key)
        retrofitData.enqueue(object : Callback<SongData?> {
            override fun onResponse(call: Call<SongData?>, response: Response<SongData?>) {
//                Log.d("TAG", "onResponse: Server response: ${response.toString()}")
//                Log.d("TAG", "recerved infomation: ${response.body().toString()}")


                var url: String = "https:"+"${response.body()!!.data.source?.lowMusic!!}"
//                Log.d("TAG", "onResponse: ${response.body()?.data?.id}")

                listSong?.add(Song(response.body()!!.data.id, response.body()?.data?.titles!!, response.body()!!.data.artist, url, response.body()!!.data.image))
            }
            override fun onFailure(call: Call<SongData?>, t: Throwable) {
                Log.d("TAG", "onFailure: ${t.message}")
            }
        })
    }
}