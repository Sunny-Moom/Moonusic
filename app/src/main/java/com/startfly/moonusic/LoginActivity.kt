package com.startfly.moonusic

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (isValidCredentials(username, password)) {
                // 登录成功，保存登录状态
                saveLoginStatus(username)
                // 跳转到主页或其他目标页面
                navigateToHome()
            } else {
                // 登录失败，显示错误消息
                Toast.makeText(this, "无效的用户名或密码", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            // 跳转到注册页面
            navigateToRegister()
        }
    }

    private fun isValidCredentials(username: String, password: String): Boolean {
        // 在此处验证用户名和密码的逻辑
        // 您可以根据实际需求自定义验证逻辑，例如调用 Navidrome 的 API 进行验证
        // 返回 true 表示验证通过，返回 false 表示验证失败
        return username.isNotEmpty() && password.isNotEmpty()
    }

    private fun saveLoginStatus(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun navigateToHome() {
        // 跳转到主页或其他目标页面的逻辑
        // 您可以根据您的应用设计进行相应的跳转
        // 以下是一个示例代码，用于跳转到名为 HomeActivity 的主页
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        // 跳转到注册页面的逻辑
        // 您可以根据您的应用设计进行相应的跳转
        // 以下是一个示例代码，用于跳转到名为 RegisterActivity 的注册页面
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
