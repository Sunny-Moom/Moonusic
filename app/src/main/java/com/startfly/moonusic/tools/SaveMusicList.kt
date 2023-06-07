package com.startfly.moonusic.tools

import com.startfly.moonusic.fragment.AllHome.MusicAll
import com.startfly.moonusic.net.GetToken
import com.startfly.moonusic.net.RequestHelper

class SaveMusicList {
    fun savelist(mmlist:MutableList<MusicAll>,nowpp:String){
        val tk= GetToken(UserMiss.password)
        val salt=tk.salt
        val token=tk.token
        val smurl=UserMiss.url+"rest/"
        val url=smurl+"savePlayQueue.view"
        var tokenMap= mapOf<String,String>(
            "u" to UserMiss.username,
            "t" to token,
            "s" to salt,
            "v" to "1.13.0",
            "c" to "Moonusic",
            "f" to  "json"
        )
        for (music in mmlist) {
            val musicId = music.MusicId
            tokenMap=tokenMap.plus(mapOf("id" to musicId))
        }
        tokenMap=tokenMap.plus(mapOf("current" to nowpp,"postition" to "0"))
        RequestHelper().get(url, tokenMap) { result ->
            result.fold(
                onSuccess = { data -> println("GET response: 保存成功") },
                onFailure = { error -> println("GET error: ${error.message}") }
            )
        }
    }
}