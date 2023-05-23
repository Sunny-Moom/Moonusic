package com.startfly.moonusic.activity


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.startfly.moonusic.fragment.HomeFragment
import com.startfly.moonusic.R
import com.startfly.moonusic.fragment.SearchFragment

class HomeActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        var shareImage: ImageView = findViewById(R.id.logo)
        ViewCompat.setTransitionName(shareImage, "shareImage")
        val token = intent.getStringExtra("token")
        val name = intent.getStringExtra("name")
        val subsonicSalt = intent.getStringExtra("subsonicSalt")
        val subsonicToken = intent.getStringExtra("subsonicToken")
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

            else -> return super.onOptionsItemSelected(item)
        }
    }
}