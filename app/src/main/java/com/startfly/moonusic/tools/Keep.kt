package com.startfly.moonusic.tools

import com.startfly.moonusic.net.RequestHelper
import com.startfly.moonusic.net.Seturl

class Keep {
    fun keep(id:String){
        val url=Seturl().setUrl(UserMiss.username,UserMiss.password, params = mapOf("id" to id,"submission" to "true"),"scrobble")
        RequestHelper().get(url) { result ->
            result.fold(
                onSuccess = { data -> println("POST response: $data") },
                onFailure = { error -> println("POST error: ${error.message}") }
            )
        }
    }
}