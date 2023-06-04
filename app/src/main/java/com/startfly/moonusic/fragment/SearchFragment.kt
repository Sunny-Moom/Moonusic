package com.startfly.moonusic.fragment

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.startfly.moonusic.R
import com.startfly.moonusic.fragment.AllHome.MusicAll
import com.startfly.moonusic.net.Seturl
import com.startfly.moonusic.tools.AllMusic
import com.startfly.moonusic.tools.SearchMusic
import com.startfly.moonusic.tools.UserMiss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var more: ImageView
    private lateinit var backtop: ImageView
    private lateinit var searchText:EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        GlobalScope.launch(Dispatchers.Main) {
            progressBar = rootView.findViewById(R.id.searchBar)
            more=rootView.findViewById(R.id.loadMoreButton)
            backtop=rootView.findViewById(R.id.resetButton)
            recyclerView = rootView.findViewById(R.id.searchView)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            val songList = SearchMusic().SearchAllMusic("",1)
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
                    }else{
                        more.visibility = View.GONE
                        backtop.visibility=View.GONE
                    }
                }
            })
            setupButtonListeners(rootView)
        }
        return rootView
    }
    private fun loadMoreData(scText: String) {
        GlobalScope.launch(Dispatchers.Main) {
            // 根据当前页码和每页数据量获取新数据
            val newDataList = getDataList(adapter.getCurrentPage() + 1, adapter.getPageSize(),scText)
            adapter.loadMoreData(newDataList) // 将新数据添加到Adapter中
        }
    }
    private fun setupButtonListeners(rootView: View) {
        val loadMoreButton: ImageView = rootView.findViewById(R.id.loadMoreButton)
        val resetButton: ImageView = rootView.findViewById(R.id.resetButton)
        val seachButton: ImageButton=rootView.findViewById(R.id.ibSearch)
        searchText=rootView.findViewById(R.id.etSearch)
        searchText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // 执行你想要的操作
                resetData(searchText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        loadMoreButton.setOnClickListener {
            loadMoreData(searchText.text.toString())
        }
        resetButton.setOnClickListener {
            resetData(searchText.text.toString())
        }
        seachButton.setOnClickListener {
            resetData(searchText.text.toString())
        }
    }

    private fun resetData(scText:String) {
        GlobalScope.launch(Dispatchers.Main) {
            // 获取第一页数据
            val newDataList = getDataList(1, adapter.getPageSize(),scText)
            adapter.resetData(newDataList) // 重置Adapter中的数据
        }
    }

    private suspend fun getDataList(page: Int, pageSize: Int,scText:String=""): List<MusicAll> {
        // 根据页码和每页数据量从服务器或本地数据库获取数据
        return SearchMusic().SearchAllMusic(scText,page)
        // 并返回数据列表
    }
    private class MyAdapter(private val dataList: MutableList<MusicAll>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        private var currentPage = 1 // 当前页码
        private val pageSize = 10 // 每页数据量

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
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
            private val artistNameTextView: TextView = itemView.findViewById(R.id.artistNameTextView)
            private val albumNameTextView: TextView = itemView.findViewById(R.id.albumNameTextView)
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)
            private val playButton: ImageView = itemView.findViewById(R.id.playButton)

            fun bind(data: MusicAll) {
                songNameTextView.text = data.MusicName
                artistNameTextView.text = data.ArtistName
                albumNameTextView.text = data.AlbumName
                val idMap= mapOf(
                    "id" to "al-"+data.AlbumId,
                    "size" to "200"
                )
                val signature = ObjectKey(data.AlbumId)
                val url = Seturl().setUrl(UserMiss.username, UserMiss.password,idMap,"getCoverArt")
                val context: Context =itemView.context
                Glide.with(context)
                    .load(url)
                    .signature(signature)
                    .into(imageView)
                playButton.setOnClickListener { /*处理播放按钮点击事件*/ }
            }
        }
    }

}
