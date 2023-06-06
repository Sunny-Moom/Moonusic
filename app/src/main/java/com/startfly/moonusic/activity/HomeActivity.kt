package com.startfly.moonusic.activity


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.startfly.moonusic.ExoPlayerService
import com.startfly.moonusic.R
import com.startfly.moonusic.fragment.HomeFragment
import com.startfly.moonusic.fragment.SearchFragment
import com.startfly.moonusic.server.ExoPlayerServiceManager
import com.startfly.moonusic.tools.MusicList
import com.startfly.moonusic.tools.UserMiss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.content.IntentFilter
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.navigation.NavigationView
import com.startfly.moonusic.fragment.AllHome.MusicAll
import com.startfly.moonusic.net.Seturl
import com.startfly.moonusic.tools.Keep

class HomeActivity : AppCompatActivity() {
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    var exoPlayerServiceManager: ExoPlayerServiceManager = ExoPlayerServiceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        exoPlayerServiceManager.initialize(this)
        var shareImage: ImageView = findViewById(R.id.logo)
        ViewCompat.setTransitionName(shareImage, "shareImage")
        UserMiss.username = intent.getStringExtra("username").toString()
        UserMiss.password = intent.getStringExtra("password").toString()
        UserMiss.token = intent.getStringExtra("token").toString()
        UserMiss.name = intent.getStringExtra("name").toString()
        UserMiss.subsonicSalt = intent.getStringExtra("subsonicSalt").toString()
        UserMiss.subsonicToken = intent.getStringExtra("subsonicToken").toString()
        GlobalScope.launch(Dispatchers.Main) {
            MusicList().getPlayerList { songList, nowplay, playtime ->
                exoPlayerServiceManager.setPlaylist(songList)
                exoPlayerServiceManager.musicJump(nowplay)
            }
        }
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
                    UserMiss.searchtxt = ""
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, searchFragment).commit()
                    true
                }

                else -> false
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, homeFragment).commit()
        val playbackStateChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ExoPlayerService.ACTION_PLAYBACK_STATE_CHANGED) {
                    val nnnPlay = exoPlayerServiceManager.getPlayNow()
                    if (nnnPlay!=null){
                        val idMap = mapOf(
                            "id" to "al-" + nnnPlay.AlbumId,
                            "size" to "200"
                        )
                        val signature = ObjectKey(nnnPlay.AlbumId)
                        val url =
                            Seturl().setUrl(UserMiss.username, UserMiss.password, idMap, "getCoverArt")
                        Glide.with(this@HomeActivity)
                            .load(url)
                            .signature(signature)
                            .into(shareImage)
                        navigationView.menu.findItem(R.id.navigation_music).title=nnnPlay.MusicName
                    }
                    Keep().keep(HomeActivity().exoPlayerServiceManager.getPlaybackMediaId().toString())
                }
            }
        }

        val filter = IntentFilter(ExoPlayerService.ACTION_PLAYBACK_STATE_CHANGED)
        registerReceiver(playbackStateChangeReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解绑ExoPlayerService
        ExoPlayerServiceManager.release(this)
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

            R.id.navigation_exit -> {
                System.exit(0)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}