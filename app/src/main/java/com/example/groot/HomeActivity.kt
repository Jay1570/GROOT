package com.example.groot

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.groot.adapter.HomeViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        navView=findViewById(R.id.bottom_navigation)
        viewPager = findViewById(R.id.viewPager)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        window.navigationBarColor = getColor(R.color.md_theme_surfaceContainer)

        viewPager.adapter = HomeViewPagerAdapter(this)

        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> viewPager.currentItem = 0
                R.id.profile -> viewPager.currentItem = 1
                R.id.help -> viewPager.currentItem = 2
                R.id.settings -> viewPager.currentItem = 3
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position) {
                    0 -> navView.selectedItemId = R.id.home
                    1 -> navView.selectedItemId = R.id.profile
                    2 -> navView.selectedItemId = R.id.help
                    3 -> navView.selectedItemId = R.id.settings
                }
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}