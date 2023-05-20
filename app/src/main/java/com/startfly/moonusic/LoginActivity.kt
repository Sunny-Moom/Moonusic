package com.startfly.moonusic

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 初始化 SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // 初始化视图
        usernameEditText = findViewById(R.id.etUsername)
        passwordEditText = findViewById(R.id.etPassword)
        val loginButton: Button = findViewById(R.id.btnLogin)
        loginButton.setOnClickListener { login() }

        // 检查是否存在存储的用户名和密码
        val storedUsername = sharedPreferences.getString("username", "")
        val storedPassword = sharedPreferences.getString("password", "")
        if (!storedUsername.isNullOrEmpty() && !storedPassword.isNullOrEmpty()) {
            usernameEditText.setText(storedUsername)
            passwordEditText.setText(storedPassword)
            login()
        }
    }

    private fun login() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        // 执行登录请求
        val client = OkHttpClient()
        val loginUrl = "http://music.sunnymoom.top/auth/login"
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
                // 登录请求失败的处理逻辑
                runOnUiThread {
                    retryLogin()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                if (response.isSuccessful && responseData != null) {
                    val jsonObject = JSONObject(responseData)
                    val token = jsonObject.getString("token")
                    val name = jsonObject.getString("name")
                    val subsonicSalt = jsonObject.getString("subsonicSalt")
                    val subsonicToken = jsonObject.getString("subsonicToken")

                    // 保存用户名和密码到 SharedPreferences
                    sharedPreferences.edit()
                        .putString("username", username)
                        .putString("password", password)
                        .apply()

                    // 跳转到主界面，并传递相关数据
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.putExtra("token", token)
                    intent.putExtra("name", name)
                    intent.putExtra("subsonicSalt", subsonicSalt)
                    intent.putExtra("subsonicToken", subsonicToken)
                    startActivity(intent)
                    finish()
                } else {
                    // 登录失败的处理逻辑
                    runOnUiThread {
                        retryLogin()
                    }
                }
            }
        })
    }

    private fun retryLogin() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("登录失败")
        dialogBuilder.setMessage("请重新输入用户名和密码")
        dialogBuilder.setPositiveButton("确定") { dialog, _ ->
            // 清除 SharedPreferences 中的用户名和密码
            sharedPreferences.edit()
                .remove("username")
                .remove("password")
                .apply()
            // 清空输入框
            usernameEditText.text = null
            passwordEditText.text = null
            dialog.dismiss()
        }
        dialogBuilder.setCancelable(false)
        dialogBuilder.show()
    }
}
