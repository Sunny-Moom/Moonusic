package com.startfly.moonusic.tools

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.startfly.moonusic.fragment.AllHome.MusicAll
import com.startfly.moonusic.net.Seturl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class MusicLists {
    suspend fun GetMusicListss(id:String,page:Int): MutableList<MusicAll> = withContext(Dispatchers.IO) {
        val headers:Map<String, String> = mapOf(
            "accept" to "application/json",
            "x-nd-authorization" to "Bearer "+UserMiss.token
        )
        val params = mapOf(
            "_end" to (page*10).toString(),
            "_order" to "ASC",
            "_sort" to "id",
            "_start" to ((page-1)*10).toString(),
            "playlist_id" to id
        )

        val songList = mutableListOf<MusicAll>()
        val url = Seturl().buildUrlWithParams(UserMiss.url+"api/playlist/"+id+"/tracks",params)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .apply { headers.forEach { header -> addHeader(header.key, header.value) } }
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body.string()
            val jsdt:String= responseBody
            val data = JsonParser.parseString(jsdt).asJsonArray
            val objects: Array<JsonObject?> = arrayOfNulls(data.size())
            for (i in 0 until data.size()) {
                objects[i] = data.get(i) as JsonObject
                songList.add(
                    MusicAll(
                        objects[i]?.get("title").toString().removeSurrounding("\""),
                        objects[i]?.get("mediaFileId").toString().removeSurrounding("\""),
                        objects[i]?.get("artist").toString().removeSurrounding("\""),
                        objects[i]?.get("album").toString().removeSurrounding("\""),
                        objects[i]?.get("albumId").toString().removeSurrounding("\"")
                    )
                )
            }
        } else {
            println("请求失败，状态码: ${response.code}")
        }
        songList
    }
}