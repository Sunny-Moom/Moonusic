package com.startfly.moonusic

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.startfly.moonusic.fragment.AllHome.MusicAll
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class ExoPlayerService : Service() {

    private val binder = ExoPlayerBinder()
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var concatenatingMediaSource: ConcatenatingMediaSource
    private lateinit var mediaSourceFactory: DefaultMediaSourceFactory
    private var currentMediaId: String? = null

    companion object {
        const val ACTION_PLAYBACK_STATE_CHANGED = "com.startfly.myapplication.PLAYBACK_STATE_CHANGED"
        const val EXTRA_CURRENT_MEDIA_ID = "com.startfly.myapplication.CURRENT_MEDIA_ID"
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    // 初始化播放器
    private fun initializePlayer() {
        dataSourceFactory = DefaultDataSourceFactory(this, "ExoPlayerService")
        concatenatingMediaSource = ConcatenatingMediaSource()
        exoPlayer = SimpleExoPlayer.Builder(this)
            .setLoadControl(DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                )
                .build())
            .build().apply {
                addListener(object : Player.Listener {
                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        super.onMediaItemTransition(mediaItem, reason)
                        currentMediaId = mediaItem?.mediaId
                        sendPlaybackStateChangeBroadcast()
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        // 在此处处理播放错误
                    }
                })
            }
        mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
    }

    // 释放播放器资源
    private fun releasePlayer() {
        exoPlayer.release()
    }

    // 设置播放列表
    fun setPlaylist(mediaItems: List<MediaItem>) {
        concatenatingMediaSource.clear()
        mediaItems.forEach { mediaItem ->
            concatenatingMediaSource.addMediaSource(mediaSourceFactory.createMediaSource(mediaItem))
        }
        exoPlayer.setMediaSource(concatenatingMediaSource)
        exoPlayer.prepare()
    }
    fun getDuration(): Long {
        return exoPlayer.duration
    }

    // 设置播放列表并跳转到指定位置
    fun setPlaylistWithPosition(mediaItems: List<MediaItem>) {
        val position: Long = exoPlayer.currentPosition
        concatenatingMediaSource.clear()
        mediaItems.forEach { mediaItem ->
            concatenatingMediaSource.addMediaSource(mediaSourceFactory.createMediaSource(mediaItem))
        }
        exoPlayer.setMediaSource(concatenatingMediaSource)
        exoPlayer.prepare()
        exoPlayer.seekTo(position.toInt(), 0)
    }

    // 在当前媒体项后插入媒体项
    fun insertNext(mediaItem: MediaItem,bl:Boolean) {
        var cc:Boolean=true
        val mediaItemCount = concatenatingMediaSource.size
        for (i in 0 until mediaItemCount) {
            val mediaItemx = concatenatingMediaSource.getMediaSource(i).mediaItem
            if (mediaItemx.mediaId == mediaItem.mediaId) {
                exoPlayer.seekTo(i, 0)
                cc=false
                break
            }
        }
        if (cc){
            if (concatenatingMediaSource.size==0){
                setPlaylist(mutableListOf<MediaItem>(mediaItem))
                play()
            }else{
                if (bl){
                    val currentIndex = exoPlayer.currentWindowIndex
                    val nextIndex = currentIndex + 1
                    concatenatingMediaSource.addMediaSource(nextIndex, mediaSourceFactory.createMediaSource(mediaItem))
                    exoPlayer.prepare()
                    Handler().postDelayed({
                        if (concatenatingMediaSource.getSize() > nextIndex && concatenatingMediaSource.getMediaSource(nextIndex).mediaItem.mediaId == mediaItem.mediaId) {
                            exoPlayer.seekTo(nextIndex, 0)
                            exoPlayer.play()
                        }
                    }, 1000)
                }
                else{
                    val currentIndex = exoPlayer.currentWindowIndex
                    val nextIndex = currentIndex + 1
                    concatenatingMediaSource.addMediaSource(
                        nextIndex,
                        mediaSourceFactory.createMediaSource(mediaItem)
                    )
                    exoPlayer.prepare()
                }
            }
        }
    }
    fun seekTok(long:Long){
        exoPlayer.seekTo(long)
    }

    // 获取播放进度
    fun getPlaybackProgress(): Long {
        return exoPlayer.currentPosition
    }

    // 获取播放列表
    fun getPlaylist(): List<MusicAll> {
        val playlist = mutableListOf<MusicAll>()
        for (i in 0 until concatenatingMediaSource.size) {
            playlist.add(
                MusicAll(
                    concatenatingMediaSource.getMediaSource(i).mediaItem.mediaMetadata.title.toString(),
                    concatenatingMediaSource.getMediaSource(i).mediaItem.mediaId,
                    concatenatingMediaSource.getMediaSource(i).mediaItem.mediaMetadata.artist.toString(),
                    concatenatingMediaSource.getMediaSource(i).mediaItem.mediaMetadata.albumTitle.toString(),
                    concatenatingMediaSource.getMediaSource(i).mediaItem.mediaMetadata.albumArtist.toString()
                )
            )
        }
        return playlist
    }

    // 获取当前播放音乐信息
    fun getPlayNow(): MusicAll {
        return MusicAll(
            exoPlayer.currentMediaItem?.mediaMetadata?.title.toString(),
            exoPlayer.currentMediaItem?.mediaId.toString(),
            exoPlayer.currentMediaItem?.mediaMetadata?.artist.toString(),
            exoPlayer.currentMediaItem?.mediaMetadata?.albumTitle.toString(),
            exoPlayer.currentMediaItem?.mediaMetadata?.albumArtist.toString()
        )
    }

    // 获取当前播放的媒体项ID
    fun getPlaybackMediaId(): String? {
        return exoPlayer.currentMediaItem?.mediaId
    }

    fun getPlayZt(): Boolean {
        return exoPlayer.playWhenReady
    }

    // 获取当前播放的媒体项
    fun getPlaybackMediaItem(): MediaItem? {
        return exoPlayer.currentMediaItem
    }

    // 跳转到指定媒体项
    fun jumpToMediaItem(mediaId: String) {
        val mediaItemCount = concatenatingMediaSource.size
        for (i in 0 until mediaItemCount) {
            val mediaItem = concatenatingMediaSource.getMediaSource(i).mediaItem
            if (mediaItem.mediaId == mediaId) {
                exoPlayer.seekTo(i, 0)
                currentMediaId = mediaId
                break
            }
        }
    }

    // 发送播放状态变化的广播
    private fun sendPlaybackStateChangeBroadcast() {
        val intent = Intent(ACTION_PLAYBACK_STATE_CHANGED)
        intent.putExtra(EXTRA_CURRENT_MEDIA_ID, currentMediaId)
        sendBroadcast(intent)
    }

    // 播放
    fun play() {
        exoPlayer.playWhenReady = true
        sendPlaybackStateChangeBroadcast()
    }

    // 暂停
    fun pause() {
        exoPlayer.playWhenReady = false
        sendPlaybackStateChangeBroadcast()
    }

    // 下一首
    fun next() {
        exoPlayer.next()
        sendPlaybackStateChangeBroadcast()
    }

    // 上一首
    fun previous() {
        exoPlayer.previous()
        sendPlaybackStateChangeBroadcast()
    }

    inner class ExoPlayerBinder : Binder() {
        fun getService(): ExoPlayerService = this@ExoPlayerService
    }
}
