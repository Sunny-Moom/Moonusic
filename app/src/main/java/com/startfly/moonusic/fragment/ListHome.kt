package com.startfly.moonusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startfly.moonusic.R
import com.startfly.moonusic.activity.HomeActivity
import com.startfly.moonusic.tools.AllMusic
import com.startfly.moonusic.tools.GetListMusic
import com.startfly.moonusic.tools.Keep
import com.startfly.moonusic.tools.ListMusic
import com.startfly.moonusic.tools.SetMediaItem
import com.startfly.moonusic.tools.UserMiss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListHome : Fragment() {
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
        val rootView= inflater.inflate(R.layout.home_list, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            progressBar = rootView.findViewById(R.id.musicListBar)
            more = rootView.findViewById(R.id.loadMoreButton)
            backtop = rootView.findViewById(R.id.resetButton)
            recyclerView = rootView.findViewById(R.id.musicListView)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val songList = ListMusic().GetMusicList(1)
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

    private suspend fun getDataList(page: Int, pageSize: Int): List<MusicList> {
        // 根据页码和每页数据量从服务器或本地数据库获取数据
        return ListMusic().GetMusicList(page)
        // 并返回数据列表
    }

    private class MyAdapter(private val dataList: MutableList<MusicList>) :
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

        fun loadMoreData(newDataList: List<MusicList>) {
            val startPosition = dataList.size // 上一次数据的末尾位置
            dataList.addAll(newDataList) // 将新数据添加到原有数据的末尾
            notifyItemRangeInserted(startPosition, newDataList.size) // 通知RecyclerView插入新数据
            currentPage++ // 更新当前页码
        }

        fun resetData(newDataList: List<MusicList>) {
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

            fun bind(data: MusicList) {
                songNameTextView.text = data.ListName
                artistNameTextView.text = data.OwnerName
                albumNameTextView.text = data.SongCount+"首"
                val drawable = itemView.resources.getDrawable(R.drawable.baseline_play_circle_outline_24)
                imageView.setImageDrawable(drawable)
                imageView.setOnClickListener {
                    val searchFragment = SearchFragment()
                    UserMiss.searchtxt="#"+data.ListId
                    val activity = itemView.context as HomeActivity
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, searchFragment).commit()
                }
                playButton.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Main) {
                        val item = GetListMusic().getMusiclst(data.ListId)
                        HomeActivity().exoPlayerServiceManager.setPlaylist(SetMediaItem().setmediaitem(item))
                        HomeActivity().exoPlayerServiceManager.play()
                    }
                }
            }
        }
    }

    data class MusicList(
        val ListName: String,
        val ListId: String,
        val OwnerName: String,
        val public: String,
        val SongCount: String,
    )
}
