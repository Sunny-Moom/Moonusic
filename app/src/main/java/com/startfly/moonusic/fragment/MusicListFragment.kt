package com.startfly.moonusic.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.startfly.moonusic.ExoPlayerService
import com.startfly.moonusic.R
import com.startfly.moonusic.activity.HomeActivity
import com.startfly.moonusic.fragment.AllHome.MusicAll

class MusicListFragment : Fragment() {
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var musiclst:List<MusicAll>
    private val playbackStateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ExoPlayerService.ACTION_PLAYBACK_STATE_CHANGED) {
                // 在onReceive()方法中更新UI
                updateMusicList()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_musiclist, container, false)
        // 注册广播接收器
        val filter = IntentFilter(ExoPlayerService.ACTION_PLAYBACK_STATE_CHANGED)
        activity?.registerReceiver(playbackStateChangeReceiver, filter)
        musiclst = HomeActivity().exoPlayerServiceManager.getPlaylist()!!
        val listView = rootView.findViewById<ListView>(R.id.listView)

        // 创建适配器
        adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            musiclst.map { it.MusicName }
        ){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)

                // 获取当前项的MusicAll对象
                val music = musiclst[position]

                // 判断当前项是否为正在播放的音乐
                val isCurrentlyPlaying = music.MusicId == HomeActivity().exoPlayerServiceManager.getPlaybackMediaId()

                // 设置高亮效果
                if (isCurrentlyPlaying) {
                    // 设置高亮样式，比如改变背景色或者字体颜色等
                    view.setBackgroundColor(Color.parseColor("#E8DEF8"))
                } else {
                    // 恢复默认样式
                    view.setBackgroundColor(Color.parseColor("#F1EBF5"))
                }
                return view
            }
        }

        // 设置适配器
        listView.adapter = adapter

        // 设置ListView的点击事件监听器
        listView.setOnItemClickListener { parent, view, position, id ->
            // 处理项点击事件
            val selectedMusic = musiclst[position]
            // 在这里可以根据需要执行相应的操作，比如跳转到播放界面或者显示详细信息等
            HomeActivity().exoPlayerServiceManager.musicJump(selectedMusic.MusicId)
            // selectedMusic变量包含了被点击项的MusicAll对象

        }

        return rootView
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // 在销毁Fragment视图时取消广播接收器的注册
        activity?.unregisterReceiver(playbackStateChangeReceiver)
    }
    fun updateMusicList() {
        musiclst = HomeActivity().exoPlayerServiceManager.getPlaylist()!!
        // 刷新适配器
        requireActivity().runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }
}