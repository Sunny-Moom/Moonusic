package com.startfly.moonusic.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.startfly.moonusic.R
import com.startfly.moonusic.net.RequestHelper
import com.startfly.moonusic.tools.UserMiss

class RegisterActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val shareImage: ImageView = findViewById(R.id.logo)
        usernameEditText = findViewById(R.id.etUsername)
        passwordEditText = findViewById(R.id.etPassword)
        val nextpassword: EditText = findViewById(R.id.etNext)
        val loginButton: Button = findViewById(R.id.btnLogin)
        ViewCompat.setTransitionName(shareImage, "shareImage")
        ViewCompat.setTransitionName(usernameEditText, "shareText1")
        ViewCompat.setTransitionName(passwordEditText, "shareText2")
        ViewCompat.setTransitionName(loginButton, "shareButton")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loginButton.setOnClickListener {
            val enpassword = passwordEditText.text.toString()
            val nepassword = nextpassword.text.toString()
            if (enpassword == nepassword) {
                register()
            } else {
                retryPassword()
            }
        }
    }

    fun register() {
        var admtoken: String = ""
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val request = RequestHelper()
        val admus = "admin"
        val admpw = "Aa114514"
        fun registerssh() {
            request.post(
                UserMiss.url+"api/user",
                headers = mapOf("accept" to "application/json", "x-nd-authorization" to admtoken),
                json = "{\"isAdmin\": false, \"userName\": \"$username\", \"name\": \"$username\", \"password\": \"$password\"}"
            ) { result ->
                result.fold(
                    onSuccess = { data -> runOnUiThread { sucesRegister() } },
                    onFailure = { error -> retryRegister() }
                )
            }
        }
        request.post(
            UserMiss.url+"auth/login",
            json = "{\"username\": \"$admus\",\"password\":\"$admpw\"}"
        ) { result ->
            result.fold(
                onSuccess = { data ->
                    val jo = jsonwork(data.toString())
                    admtoken = jo.get("token").toString()
                    admtoken = admtoken.replace("\"", "");
                    admtoken = "Bearer " + admtoken
                    println(admtoken)
                    registerssh()
                },
                onFailure = { error -> retryRegister() }
            )
        }
    }

    fun jsonwork(json: String): JsonObject {
        var jo: JsonObject = JsonParser.parseString(json).getAsJsonObject()
        return jo
    }

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

    private fun sucesRegister() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("注册成功")
        dialogBuilder.setMessage("返回登录页面进行登录")
        dialogBuilder.setPositiveButton("确定") { dialog, _ ->
            dialog.dismiss()
            onBackPressed()
        }
        dialogBuilder.setCancelable(false)
        dialogBuilder.show()
    }

    private fun retryPassword() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("两次输入密码不一致")
        dialogBuilder.setMessage("请再次输入")
        dialogBuilder.setPositiveButton("确定") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setCancelable(false)
        dialogBuilder.show()
    }

    private fun retryRegister() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("注册失败")
        dialogBuilder.setMessage("可能是网络异常,或者是用户名重复。")
        dialogBuilder.setPositiveButton("请重试") { dialog, _ ->
            dialog.dismiss()
        }
        dialogBuilder.setCancelable(false)
        dialogBuilder.show()
    }
}