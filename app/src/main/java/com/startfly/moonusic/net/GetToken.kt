package com.startfly.moonusic.net

import java.security.MessageDigest

class GetToken(password: String) {
    // 生成指定长度的随机字符串
    fun randomString(length: Int): String {
        val letters = "abcdefghijklmnopqrstuvwxyz"
        return (1..length).map { letters.random() }.joinToString("")
    }

    // 生成长度为6的随机字符串作为salt
    val salt = randomString(6)

    // 将密码和随机字符串进行拼接
    val passwordAndSalt = password + salt

    // 计算MD5哈希值
    val md5Hash = MessageDigest.getInstance("MD5").digest(passwordAndSalt.toByteArray()).joinToString("") { "%02x".format(it) }

    // 生成 Subsonic API 认证令牌
    val token = md5Hash

}