package com.startfly.moonusic.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.startfly.moonusic.R

class RegisterActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        var shareImage: ImageView = findViewById(R.id.logo)
        val usernameEditText: EditText = findViewById(R.id.etUsername)
        val passwordEditText: EditText = findViewById(R.id.etPassword)
        val loginButton: Button = findViewById(R.id.btnLogin)
        ViewCompat.setTransitionName(shareImage, "shareImage")
        ViewCompat.setTransitionName(usernameEditText,"shareText1")
        ViewCompat.setTransitionName(passwordEditText,"shareText2")
        ViewCompat.setTransitionName(loginButton,"shareButton")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    // 在你的Activity中
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 处理返回键
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}