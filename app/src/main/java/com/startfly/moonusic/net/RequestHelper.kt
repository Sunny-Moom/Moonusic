package com.startfly.moonusic.net

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class RequestHelper {
    private val client = OkHttpClient()

    fun get(url: String, headers: Map<String, String> = emptyMap(), params: Map<String, String> = emptyMap(), callback: (Result<String>) -> Unit) {
        val requestUrl = buildUrlWithParams(url, params)
        val request = Request.Builder()
            .url(requestUrl)
            .apply { headers.forEach { header -> addHeader(header.key, header.value) } }
            .build()

        asyncRequest(request, callback)
    }

    fun post(url: String, headers: Map<String, String> = emptyMap(), params: Map<String, String> = emptyMap(), json: String = "", callback: (Result<String>) -> Unit) {
        val requestUrl = buildUrlWithParams(url, params)
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .apply { headers.forEach { header -> addHeader(header.key, header.value) } }
            .build()

        asyncRequest(request, callback)
    }

    private fun buildUrlWithParams(url: String, params: Map<String, String>): String {
        val httpUrl = url.toHttpUrlOrNull()?.newBuilder()
        params.forEach { (key, value) -> httpUrl?.addQueryParameter(key, value) }
        return httpUrl?.build().toString()
    }

    private fun asyncRequest(request: Request, callback: (Result<String>) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        Result.success(response.body?.string() ?: "")
                    } else {
                        Result.failure(IOException("Unexpected code $response"))
                    }
                } catch (e: IOException) {
                    Result.failure(e)
                }
            }
            callback(result)
        }
    }
}