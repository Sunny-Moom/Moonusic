package com.startfly.moonusic

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

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

    val navidromeUrl = "http://music.sunnymoom.top"

    private fun isValidCredentials(username: String, password: String): Boolean {
        // 在此处验证用户名和密码的逻辑
        val client = OkHttpClient()

        val loginUrl = "$navidromeUrl/auth/login"
        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(loginUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    val jsonObject = JSONObject(responseData)
                    val token = jsonObject.getString("token")

                    // 在此处处理返回的 JSON 数据
                    // 您可以根据需要提取所需的值并执行相应的操作

                    // 例如，将 token 保存到 SharedPreferences 或全局变量中供后续使用
                    // sharedPreferences.edit().putString("token", token).apply()
                    // 或者将其他值传递给登录后的主界面
                    // navigateToHome(jsonObject)
                } else {
                    // 处理登录失败的情况
                    // 在此处显示错误消息或执行其他操作
                }
            }
        })

        // 由于请求是异步的，因此无法立即返回验证结果
        // 在此处可以返回默认值（例如 false），或者不返回任何值，具体取决于您的需求
        return false
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
