package com.startfly.moonusic.tools

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.startfly.moonusic.fragment.NewHome.Song
import com.startfly.moonusic.net.Seturl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class NewAlbumld {
    suspend fun GetNewAlbumld(): MutableList<Song> = withContext(Dispatchers.IO) {
        val params = mapOf(
            "type" to "newest",
            "size" to "20"
        )
        val songList = mutableListOf<Song>()
        val url = Seturl().setUrl(UserMiss.username, UserMiss.password, params, "getAlbumList2")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            val jsdt: String = responseBody.toString()
            val data = parseJson(jsdt)
            val objects: Array<JsonObject?> = arrayOfNulls(data.size())
            for (i in 0 until data.size()) {
                objects[i] = data.get(i) as JsonObject
                songList.add(
                    Song(
                        objects[i]?.get("name").toString().removeSurrounding("\""),
                        objects[i]?.get("id").toString().removeSurrounding("\""),
                        objects[i]?.get("artist").toString().removeSurrounding("\""),
                        objects[i]?.get("name").toString().removeSurrounding("\""),
                        objects[i]?.get("id").toString().removeSurrounding("\"")
                    )
                )
            }
        } else {
            println("请求失败，状态码: ${response.code}")
        }
        songList
    }

    fun parseJson(json: String): JsonArray {
        var jo: JsonObject = JsonParser.parseString(json).getAsJsonObject()
        jo = jo.get("subsonic-response") as JsonObject
        jo = jo.get("albumList2") as JsonObject
        val ao = jo.get("album") as JsonArray
        return ao
    }
}
