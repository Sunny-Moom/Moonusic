package com.startfly.moonusic.tools

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.startfly.moonusic.fragment.AllHome.MusicAll
import com.startfly.moonusic.net.Seturl

class SetMediaItem {
    fun setmediaitem(mlist:MutableList<MusicAll>): MutableList<MediaItem> {
        var songList:MutableList<MediaItem> = mutableListOf()
        for (i in 0 until mlist.size){
            songList.add(
                MediaItem.Builder()
                    .setUri(getSong(mlist[i].MusicId))
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(mlist[i].MusicName)
                            .setArtist(mlist[i].ArtistName)
                            .setAlbumTitle(mlist[i].AlbumName)
                            .setAlbumArtist(mlist[i].AlbumId)
                            .build()
                    )
                    .setMediaId(mlist[i].MusicId)
                    .build()
            )
        }
        return songList
    }
    private fun getSong(id: String): String {
        val idMap= mapOf(
            "id" to id
        )
        return Seturl().setUrl(UserMiss.username,UserMiss.password,idMap,"stream")
    }
}