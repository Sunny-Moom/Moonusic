package com.startfly.moonusic.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.startfly.moonusic.ExoPlayerService
import com.startfly.moonusic.R
import com.startfly.moonusic.activity.HomeActivity
import com.startfly.moonusic.net.Seturl
import com.startfly.moonusic.tools.UserMiss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MusicFragment: Fragment() {
    private lateinit var musicimg:ImageView
    private lateinit var artistname:TextView
    private lateinit var albumname:TextView
    private lateinit var seekbar:SeekBar
    private lateinit var musiccur:TextView
    private lateinit var musiclength:TextView
    private lateinit var ivplay:ImageView
    private lateinit var ivpause:ImageView
    private lateinit var ivprevious:ImageView
    private lateinit var ivnext:ImageView

    private val playbackStateChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ExoPlayerService.ACTION_PLAYBACK_STATE_CHANGED) {
                // 在onReceive()方法中更新UI
                updateUI()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView= inflater.inflate(R.layout.fragment_music, container, false)
        // 注册广播接收器
        val filter = IntentFilter(ExoPlayerService.ACTION_PLAYBACK_STATE_CHANGED)
        activity?.registerReceiver(playbackStateChangeReceiver, filter)
        musicimg=rootView.findViewById(R.id.music_img)
        artistname=rootView.findViewById(R.id.artist_name)
        albumname=rootView.findViewById(R.id.album_name)
        seekbar=rootView.findViewById(R.id.seek_bar)
        musiccur=rootView.findViewById(R.id.music_cur)
        musiclength=rootView.findViewById(R.id.music_length)
        ivplay=rootView.findViewById(R.id.iv_play)
        ivpause=rootView.findViewById(R.id.iv_pause)
        ivprevious=rootView.findViewById(R.id.iv_previous)
        ivnext=rootView.findViewById(R.id.iv_next)
        updateUI()
        ivplay.setOnClickListener {
            HomeActivity().exoPlayerServiceManager.play()
        }
        ivpause.setOnClickListener {
            HomeActivity().exoPlayerServiceManager.pause()
        }
        ivprevious.setOnClickListener {
            HomeActivity().exoPlayerServiceManager.previous()
        }
        ivnext.setOnClickListener {
            HomeActivity().exoPlayerServiceManager.musicNext()
        }
        return rootView
    }
    override fun onDestroyView() {
        super.onDestroyView()

        // 在销毁Fragment视图时取消广播接收器的注册
        activity?.unregisterReceiver(playbackStateChangeReceiver)
    }

    // 定义一个方法用于更新UI
    private fun updateUI() {
        val nnnPlay = HomeActivity().exoPlayerServiceManager.getPlayNow()
        if (nnnPlay?.MusicName != null){
            val idMap = mapOf(
                "id" to "al-" + nnnPlay.AlbumId,
                "size" to "200"
            )
            val signature = ObjectKey(nnnPlay.AlbumId)
            val url =
                Seturl().setUrl(UserMiss.username, UserMiss.password, idMap, "getCoverArt")
            Glide.with(this)
                .load(url)
                .signature(signature)
                .into(musicimg)
            artistname.setText(nnnPlay.ArtistName)
            albumname.setText(nnnPlay.AlbumName)
            if (HomeActivity().exoPlayerServiceManager.getPlayZt()){
                ivplay.visibility=View.GONE
                ivpause.visibility=View.VISIBLE
            }else{
                ivplay.visibility=View.VISIBLE
                ivpause.visibility=View.GONE
            }
        }
    }
}