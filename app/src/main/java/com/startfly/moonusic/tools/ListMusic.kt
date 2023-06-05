package com.startfly.moonusic.tools

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.startfly.moonusic.fragment.ListHome.MusicList
import com.startfly.moonusic.net.Seturl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ListMusic {
    suspend fun GetMusicList(page:Int): MutableList<MusicList> = withContext(Dispatchers.IO) {
        val headers:Map<String, String> = mapOf(
            "accept" to "application/json",
            "Connection" to "keep-alive",
            "x-nd-authorization" to "Bearer "+UserMiss.token
        )
        val params = mapOf(
            "_end" to (page*10).toString(),
            "_order" to "ASC",
            "_sort" to "id",
            "_start" to ((page-1)*10).toString()
        )
        val songList = mutableListOf<MusicList>()
        val url = Seturl().buildUrlWithParams(UserMiss.url+"api/playlist",params)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .apply { headers.forEach { header -> addHeader(header.key, header.value) } }
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body.string()
            val jsdt:String= responseBody
            println(jsdt)
            val data = JsonParser.parseString(jsdt).asJsonArray
            val objects: Array<JsonObject?> = arrayOfNulls(data.size())
            for (i in 0 until data.size()) {
                objects[i] = data.get(i) as JsonObject
                songList.add(
                    MusicList(
                        objects[i]?.get("name").toString().removeSurrounding("\""),
                        objects[i]?.get("id").toString().removeSurrounding("\""),
                        objects[i]?.get("ownerName").toString().removeSurrounding("\""),
                        objects[i]?.get("public").toString().removeSurrounding("\""),
                        objects[i]?.get("songCount").toString().removeSurrounding("\"")
                    )
                )
            }
        } else {
            println("请求失败，状态码: ${response.code}")
        }
        songList
    }
}