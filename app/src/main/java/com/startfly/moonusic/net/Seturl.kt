package com.startfly.moonusic.net

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class Seturl {

    fun setUrl(username:String, password:String, params: Map<String, String> = emptyMap(), api:String): String {
        val tk= GetToken(password)
        val salt=tk.salt
        val token=tk.token
        val smurl="http://music.sunnymoom.top/rest/"
        val url=smurl+api
        var tokenMap= mapOf<String,String>(
            "u" to username,
            "t" to token,
            "s" to salt,
            "v" to "1.13.0",
            "c" to "test",
            "f" to  "json"
        )
        tokenMap=tokenMap.plus(params)
        return buildUrlWithParams(url,tokenMap)
    }
    fun buildUrlWithParams(url: String, params: Map<String, String>): String {
        val httpUrl = url.toHttpUrlOrNull()?.newBuilder()
        params.forEach { (key, value) -> httpUrl?.addQueryParameter(key, value) }
        return httpUrl?.build().toString()
    }
}