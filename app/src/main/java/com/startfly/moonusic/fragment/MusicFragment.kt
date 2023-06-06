package com.startfly.moonusic.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.startfly.moonusic.ExoPlayerService
import com.startfly.moonusic.R
import com.startfly.moonusic.activity.HomeActivity
import com.startfly.moonusic.net.Seturl
import com.startfly.moonusic.tools.UserMiss

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
        // 为进度条设置监听器
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // 更新当前播放进度的显示
                musiccur.text = formatTime(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // 将进度条的当前进度设置为当前播放进度
                HomeActivity().exoPlayerServiceManager.seekTo(seekBar.progress.toLong())
            }
        })
        startUpdateSeekBar()
        return rootView
    }
    override fun onDestroyView() {
        super.onDestroyView()
        stopUpdateSeekBar()
        // 在销毁Fragment视图时取消广播接收器的注册
        activity?.unregisterReceiver(playbackStateChangeReceiver)
    }

    // 定义一个方法用于更新UI
    private fun updateUI() {
        val nnnPlay = HomeActivity().exoPlayerServiceManager.getPlayNow()
        if (nnnPlay?.MusicName != null){
            val idMap = mapOf(
                "id" to "al-" + nnnPlay.AlbumId,
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
            // 更新进度条
            val duration = HomeActivity().exoPlayerServiceManager.getDuration()
            musiclength.text = formatTime(duration)
            seekbar.max = duration.toInt()
            seekbar.progress = HomeActivity().exoPlayerServiceManager.getCurrentPosition().toInt()
        }
    }
    private val handler = Handler()
    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            // 更新进度条
            SeekBarUpdata()

            // 延迟1秒后再次执行
            handler.postDelayed(this, 1000)
        }
    }
    private fun SeekBarUpdata(){
        // 更新进度条
        val duration = HomeActivity().exoPlayerServiceManager.getDuration()
        val currentPosition = HomeActivity().exoPlayerServiceManager.getCurrentPosition()
        val progress = currentPosition.toInt()
        musiclength.text = formatTime(duration)
        seekbar.max = duration.toInt()
        seekbar.progress = progress
        musiccur.text = formatTime(currentPosition)
    }
    // 启动定时更新进度条
    private fun startUpdateSeekBar() {
        handler.postDelayed(updateSeekBarRunnable, 1000)
    }
    // 停止定时更新进度条
    private fun stopUpdateSeekBar() {
        handler.removeCallbacks(updateSeekBarRunnable)
    }
    // 将毫秒数转换为"时:分:秒"格式的字符串
    private fun formatTime(time: Long): String {
        val hour = time / 3600000
        val minute = (time - hour * 3600000) / 60000
        val second = (time - hour * 3600000 - minute * 60000) / 1000
        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }
}