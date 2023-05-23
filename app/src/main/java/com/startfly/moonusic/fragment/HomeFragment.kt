package com.startfly.moonusic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.startfly.moonusic.R

class HomeFragment : Fragment() {

    private lateinit var pager: ViewPager2
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 初始化 ViewPager2 和 BottomNavigationView
        pager = view.findViewById(R.id.pager)
        bottomNav = view.findViewById(R.id.bottom_home)

        // 设置 ViewPager2 的适配器
        pager.adapter = HomePagerAdapter(requireActivity())

        // 将 BottomNavigationView 与 ViewPager2 关联起来
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_all -> {
                    pager.currentItem = 2
                    true
                }
                R.id.navigation_list -> {
                    pager.currentItem = 1
                    true
                }
                R.id.navigation_new -> {
                    pager.currentItem = 0
                    true
                }
                R.id.navigation_history -> {
                    pager.currentItem = 3
                    true
                }
                else -> false
            }
        }

        // 监听 ViewPager2 的页面切换事件，同步更新 BottomNavigationView
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNav.menu.getItem(position).isChecked = true
            }
        })

        return view
    }
    class HomePagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        private val fragmentList = listOf(NewHome(), ListHome(), AllHome(), HistoryHome())

        override fun getItemCount(): Int = fragmentList.size

        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }
}
