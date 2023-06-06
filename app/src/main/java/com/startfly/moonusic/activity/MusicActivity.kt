package com.startfly.moonusic.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.startfly.moonusic.R
import com.startfly.moonusic.fragment.MusicFragment
import com.startfly.moonusic.fragment.MusicListFragment

class MusicActivity : AppCompatActivity()  {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        // 创建 FragmentAdapter
        val adapter = FragmentAdapter(supportFragmentManager)
        adapter.addFragment(MusicFragment(), "播放")
        adapter.addFragment(MusicListFragment(), "音乐列表")
        supportActionBar?.title = "Music"
        // 设置 Adapter
        viewPager.adapter = adapter

        // 配置 TabLayout
        tabLayout.setupWithViewPager(viewPager)
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
    override fun onBackPressed() {
        // 创建一个新的Intent，将其设置为FLAG_ACTIVITY_REORDER_TO_FRONT标志，
        // 然后使用startActivity()方法启动新的Activity，以返回到上一个Activity。
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
    }

    // FragmentAdapter
    class FragmentAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitleList = mutableListOf<String>()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
    }

}