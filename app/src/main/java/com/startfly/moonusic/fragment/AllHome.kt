package com.startfly.moonusic.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.startfly.moonusic.R
import com.startfly.moonusic.activity.HomeActivity
import com.startfly.moonusic.net.Seturl
import com.startfly.moonusic.tools.AllMusic
import com.startfly.moonusic.tools.SetMediaItem
import com.startfly.moonusic.tools.UserMiss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AllHome : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var more: ImageView
    private lateinit var backtop: ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.home_all, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            progressBar = rootView.findViewById(R.id.musicAllBar)
            more = rootView.findViewById(R.id.loadMoreButton)
            backtop = rootView.findViewById(R.id.resetButton)
            recyclerView = rootView.findViewById(R.id.musicAllView)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val songList = AllMusic().GetAllMusic(1)
            adapter = MyAdapter(songList)
            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition =
                        layoutManager.findLastVisibleItemPosition() // 获取最后一个可见项的位置
                    val totalItemCount = layoutManager.itemCount // 获取RecyclerView中的总项数
                    if (lastVisibleItemPosition == totalItemCount - 1 && dy > 0) { // 判断是否滚动到底部
                        more.visibility = View.VISIBLE
                        backtop.visibility = View.VISIBLE
                    } else {
                        more.visibility = View.GONE
                        backtop.visibility = View.GONE
                    }
                }
            })
            setupButtonListeners(rootView)
        }
        return rootView
    }

    private fun loadMoreData() {
        GlobalScope.launch(Dispatchers.Main) {
            // 根据当前页码和每页数据量获取新数据
            val newDataList = getDataList(adapter.getCurrentPage() + 1, adapter.getPageSize())
            adapter.loadMoreData(newDataList) // 将新数据添加到Adapter中
        }
    }

    private fun setupButtonListeners(rootView: View) {
        val loadMoreButton: ImageView = rootView.findViewById(R.id.loadMoreButton)
        val resetButton: ImageView = rootView.findViewById(R.id.resetButton)

        loadMoreButton.setOnClickListener {
            loadMoreData()
        }

        resetButton.setOnClickListener {
            resetData()
        }
    }


    private fun resetData() {
        GlobalScope.launch(Dispatchers.Main) {
            // 获取第一页数据
            val newDataList = getDataList(1, adapter.getPageSize())
            adapter.resetData(newDataList) // 重置Adapter中的数据
        }
    }

    private suspend fun getDataList(page: Int, pageSize: Int): List<MusicAll> {
        // 根据页码和每页数据量从服务器或本地数据库获取数据
        return AllMusic().GetAllMusic(page)
        // 并返回数据列表
    }

    private class MyAdapter(private val dataList: MutableList<MusicAll>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        private var currentPage = 1 // 当前页码
        private val pageSize = 10 // 每页数据量

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            holder.bind(data)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        fun loadMoreData(newDataList: List<MusicAll>) {
            val startPosition = dataList.size // 上一次数据的末尾位置
            dataList.addAll(newDataList) // 将新数据添加到原有数据的末尾
            notifyItemRangeInserted(startPosition, newDataList.size) // 通知RecyclerView插入新数据
            currentPage++ // 更新当前页码
        }

        fun resetData(newDataList: List<MusicAll>) {
            dataList.clear() // 清空原有数据
            dataList.addAll(newDataList) // 将新数据添加到数据列表
            notifyDataSetChanged() // 通知RecyclerView更新数据
            currentPage = 1 // 重置当前页码
        }

        fun getCurrentPage(): Int {
            return currentPage // 获取当前页码
        }

        fun getPageSize(): Int {
            return pageSize // 获取每页数据量
        }

        private class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val songNameTextView: TextView = itemView.findViewById(R.id.songNameTextView)
            private val artistNameTextView: TextView =
                itemView.findViewById(R.id.artistNameTextView)
            private val albumNameTextView: TextView = itemView.findViewById(R.id.albumNameTextView)
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)
            private val playButton: ImageView = itemView.findViewById(R.id.playButton)

            fun bind(data: MusicAll) {
                songNameTextView.text = data.MusicName
                artistNameTextView.text = data.ArtistName
                albumNameTextView.text = data.AlbumName
                val idMap = mapOf(
                    "id" to "al-" + data.AlbumId,
                    "size" to "200"
                )
                val signature = ObjectKey(data.AlbumId)
                val url =
                    Seturl().setUrl(UserMiss.username, UserMiss.password, idMap, "getCoverArt")
                val context: Context = itemView.context
                Glide.with(context)
                    .load(url)
                    .signature(signature)
                    .into(imageView)
                imageView.setOnClickListener {
                    HomeActivity().exoPlayerServiceManager.insertNext(
                        MediaItem.Builder()
                            .setUri(SetMediaItem().getSong(data.MusicId))
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setTitle(data.MusicName)
                                    .setArtist(data.ArtistName)
                                    .setAlbumTitle(data.AlbumName)
                                    .setAlbumArtist(data.AlbumId)
                                    .build()
                            )
                            .setMediaId(data.MusicId)
                            .build(),true)
                }
                playButton.setOnClickListener {
                    HomeActivity().exoPlayerServiceManager.insertNext(
                        MediaItem.Builder()
                        .setUri(SetMediaItem().getSong(data.MusicId))
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(data.MusicName)
                                .setArtist(data.ArtistName)
                                .setAlbumTitle(data.AlbumName)
                                .setAlbumArtist(data.AlbumId)
                                .build()
                        )
                        .setMediaId(data.MusicId)
                        .build())
                }
            }
        }
    }

    data class MusicAll(
        val MusicName: String,
        val MusicId: String,
        val ArtistName: String,
        val AlbumName: String,
        val AlbumId: String,
    )
}