package com.startfly.moonusic.server

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.startfly.moonusic.fragment.AllHome.MusicAll
import android.os.IBinder
import com.google.android.exoplayer2.MediaItem
import com.startfly.moonusic.ExoPlayerService


object ExoPlayerServiceManager {
    private lateinit var exoPlayerService: ExoPlayerService
    private var serviceBound = false

    fun initialize(context: Context) {
        val intent = Intent(context, ExoPlayerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun release(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }
    }
    fun getPlayNow():MusicAll?{
        return if (serviceBound) {
            exoPlayerService.getPlayNow()
        } else {
            null
        }
    }
    fun getPlaylist(): List<MusicAll>?{
        return if (serviceBound) {
            exoPlayerService.getPlaylist()
        } else {
            null
        }
    }

    fun setPlaylist(mediaItems: List<MediaItem>) {
        if (serviceBound) {
            exoPlayerService.setPlaylist(mediaItems)
        }
    }

    fun insertNext(mediaItem: MediaItem) {
        if (serviceBound) {
            exoPlayerService.insertNext(mediaItem)
        }
    }
    fun getPlayZt(): Boolean {
        return exoPlayerService.getPlayZt()
    }
    fun musicJump(mediaId:String){
        if (serviceBound){
            exoPlayerService.jumpToMediaItem(mediaId)
        }
    }

    fun play() {
        if (serviceBound) {
            exoPlayerService.play()
        }
    }

    fun pause() {
        if (serviceBound) {
            exoPlayerService.pause()
        }
    }

    fun musicNext() {
        if (serviceBound) {
            exoPlayerService.next()
        }
    }

    fun previous() {
        if (serviceBound) {
            exoPlayerService.previous()
        }
    }

    fun getPlaybackMediaId(): String? {
        return if (serviceBound) {
            exoPlayerService.getPlaybackMediaId()
        } else {
            null
        }
    }

    fun getPlaybackMediaItem(): MediaItem? {
        return if (serviceBound) {
            exoPlayerService.getPlaybackMediaItem()
        } else {
            null
        }
    }

    fun getPlaybackProgress(): Long {
        return if (serviceBound) {
            exoPlayerService.getPlaybackProgress()
        } else {
            0L
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ExoPlayerService.ExoPlayerBinder
            exoPlayerService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }
}
