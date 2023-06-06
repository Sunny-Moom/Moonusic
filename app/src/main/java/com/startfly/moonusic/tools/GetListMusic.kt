package com.startfly.moonusic.tools

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.startfly.moonusic.fragment.AllHome.MusicAll
import com.startfly.moonusic.net.Seturl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class GetListMusic {
    suspend fun getMusiclst(id:String): MutableList<MusicAll> = withContext(Dispatchers.IO) {
        var mlist: MutableList<MusicAll> = mutableListOf<MusicAll>()
        var urltxt = Seturl().setUrl(
            UserMiss.username,
            UserMiss.password,
            params = mapOf("id" to id),
            "getPlaylist"
        )
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urltxt)
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        val jsdt: String = responseBody.toString()
        var llist = JsonParser.parseString(jsdt).getAsJsonObject()
        llist = llist.get("subsonic-response") as JsonObject
        if (llist.has("playlist")) {
            llist = llist.get("playlist") as JsonObject
            val ao = llist.get("entry") as JsonArray
            val objects: Array<JsonObject?> = arrayOfNulls(ao.size())
            for (i in 0 until ao.size()) {
                objects[i] = ao.get(i) as JsonObject
                mlist.add(
                    MusicAll(
                        objects[i]?.get("title").toString().removeSurrounding("\""),
                        objects[i]?.get("id").toString().removeSurrounding("\""),
                        objects[i]?.get("artist").toString().removeSurrounding("\""),
                        objects[i]?.get("album").toString().removeSurrounding("\""),
                        objects[i]?.get("albumId").toString().removeSurrounding("\"")
                    )
                )
            }
        } else {
            println("请求失败，状态码: ${response.code}")
        }
        mlist
    }
}