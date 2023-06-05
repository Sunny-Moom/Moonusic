package com.startfly.moonusic.tools

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.startfly.moonusic.fragment.AllHome
import com.startfly.moonusic.net.Seturl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class SearchMusic {
    suspend fun SearchAllMusic(search:String,page:Int): MutableList<AllHome.MusicAll> = withContext(Dispatchers.IO) {
        val headers:Map<String, String> = mapOf(
            "accept" to "application/json",
            "Connection" to "keep-alive",
            "x-nd-authorization" to "Bearer "+UserMiss.token
        )
        val params = mapOf(
            "_end" to (page*10).toString(),
            "_order" to "ASC",
            "_sort" to "title",
            "_start" to ((page-1)*10).toString(),
            "title" to search
        )
        val songList = mutableListOf<AllHome.MusicAll>()
        val url = Seturl().buildUrlWithParams(UserMiss.url+"api/song",params)
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
                    AllHome.MusicAll(
                        objects[i]?.get("orderTitle").toString().removeSurrounding("\""),
                        objects[i]?.get("id").toString().removeSurrounding("\""),
                        objects[i]?.get("orderArtistName").toString().removeSurrounding("\""),
                        objects[i]?.get("orderAlbumName").toString().removeSurrounding("\""),
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