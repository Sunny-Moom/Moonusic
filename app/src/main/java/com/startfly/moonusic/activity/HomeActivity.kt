package com.startfly.moonusic.activity


import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.startfly.moonusic.fragment.HomeFragment
import com.startfly.moonusic.R
import com.startfly.moonusic.fragment.SearchFragment
import com.startfly.moonusic.tools.UserMiss

class HomeActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var shareImage: ImageView = findViewById(R.id.logo)
        ViewCompat.setTransitionName(shareImage, "shareImage")
        UserMiss.username= intent.getStringExtra("username").toString()
        UserMiss.password = intent.getStringExtra("password").toString()
        UserMiss.token = intent.getStringExtra("token").toString()
        UserMiss.name = intent.getStringExtra("name").toString()
        UserMiss.subsonicSalt = intent.getStringExtra("subsonicSalt").toString()
        UserMiss.subsonicToken = intent.getStringExtra("subsonicToken").toString()
        shareImage.setOnClickListener {
            val intent = Intent(this@HomeActivity, MusicActivity::class.java)
            startActivity(intent)
        }
        val navigationView = findViewById<BottomNavigationView>(R.id.bottom_view)
        navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, homeFragment).commit()
                    true
                }
                R.id.navigation_music -> {
                    true
                }
                R.id.navigation_search -> {
                    UserMiss.searchtxt=""
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, searchFragment).commit()
                    true
                }
                else -> false
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, homeFragment).commit()
    }
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.offline, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_output -> {
                val sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.navigation_exit->{
                System.exit(0)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}