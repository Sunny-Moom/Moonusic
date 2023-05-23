package com.startfly.moonusic.net

import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class HttpClient {
    private val client = OkHttpClient()

    fun get(url: String, headers: Map<String, String> = emptyMap(), params: Map<String, String> = emptyMap()): String {
        val requestBuilder = Request.Builder()
            .url(urlWithParams(url, params))
            .headers(headers.toHeaders())

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    fun post(url: String, headers: Map<String, String> = emptyMap(), params: Map<String, String> = emptyMap()): String {
        val requestBody = FormBody.Builder()
        params.forEach { (key, value) ->
            requestBody.add(key, value)
        }

        val requestBuilder = Request.Builder()
            .url(url)
            .headers(headers.toHeaders())
            .post(requestBody.build())

        val request = requestBuilder.build()
        val response = client.newCall(request).execute()
        return response.body?.string() ?: ""
    }

    private fun urlWithParams(url: String, params: Map<String, String>): HttpUrl {
        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()
        params.forEach { (key, value) ->
            urlBuilder?.addQueryParameter(key, value)
        }
        return urlBuilder?.build() ?: throw IllegalArgumentException("Invalid URL: $url")
    }
}

