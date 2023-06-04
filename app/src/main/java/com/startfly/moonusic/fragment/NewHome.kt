package com.startfly.moonusic.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.startfly.moonusic.R
import com.startfly.moonusic.net.Seturl
import com.startfly.moonusic.tools.NewAlbumld
import com.startfly.moonusic.tools.UserMiss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NewHome : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView=inflater.inflate(R.layout.home_new, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            val songList = NewAlbumld().GetNewAlbumld()
            recyclerView=rootView.findViewById(R.id.albumView)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            adapter= MyAdapter(songList)
            recyclerView.adapter=adapter
        }
        return rootView
    }

    private class MyAdapter(private val dataList: List<Song>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            holder.bind(data)
        }

        override fun getItemCount() = dataList.size

        private class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val songNameTextView: TextView = itemView.findViewById(R.id.songNameTextView)
            private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
            private val albumNameTextView: TextView = itemView.findViewById(R.id.albumNameTextView)
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)
            private val playButton: ImageView = itemView.findViewById(R.id.playButton)

            fun bind(data: Song) {
                songNameTextView.text = data.songName
                artistNameTextView.text = data.artistName
                albumNameTextView.text = data.albumName
                val idMap= mapOf(
                    "id" to "al-"+data.albumId,
                    "size" to "200"
                )
                val signature = ObjectKey(data.albumId)
                val url = Seturl().setUrl(UserMiss.username,UserMiss.password,idMap,"getCoverArt")
                val context:Context=itemView.context
                Glide.with(context)
                    .load(url)
                    .signature(signature)
                    .into(imageView)
                playButton.setOnClickListener { /*处理播放按钮点击事件*/ }
            }
        }
    }
    data class Song(
        val songName: String,
        val songId: String,
        val artistName: String,
        val albumName: String,
        val albumId: String,
    )
}