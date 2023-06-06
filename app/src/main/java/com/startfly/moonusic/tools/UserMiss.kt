package com.startfly.moonusic.tools

import com.startfly.moonusic.fragment.AllHome.MusicAll

class UserMiss {
    companion object{
        var url="http://music.sunnymoom.top/"
        lateinit var username:String
        lateinit var password:String
        lateinit var token:String
        lateinit var name:String
        lateinit var subsonicSalt:String
        lateinit var subsonicToken:String
        var searchtxt:String=" "
        var mzklist:MutableList<MusicAll> = mutableListOf<MusicAll>()
    }
}