package com.startfly.moonusic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val token = intent.getStringExtra("token")
        val name = intent.getStringExtra("name")
        val subsonicSalt = intent.getStringExtra("subsonicSalt")
        val subsonicToken = intent.getStringExtra("subsonicToken")
    }
}