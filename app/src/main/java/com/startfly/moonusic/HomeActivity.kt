package com.startfly.moonusic

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var shareImage:ImageView = findViewById(R.id.logo)
        ViewCompat.setTransitionName(shareImage, "shareImage")
        val token = intent.getStringExtra("token")
        val name = intent.getStringExtra("name")
        val subsonicSalt = intent.getStringExtra("subsonicSalt")
        val subsonicToken = intent.getStringExtra("subsonicToken")
    }
}