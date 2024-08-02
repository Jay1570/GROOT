package com.example.groot

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.groot.fragments.HelpFragment
import com.example.groot.fragments.HomeFragment
import com.example.groot.fragments.ProfileFragment
import com.example.groot.fragments.SettingsFragment
import com.example.groot.model.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var navview: BottomNavigationView
    private lateinit var viewpager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        navview=findViewById(R.id.bottom_navigation)
        viewpager=findViewById(R.id.view_pager)


        viewpager.adapter= ViewPagerAdapter(this)
        navview.setOnItemSelectedListener(){
            when(it.itemId){
                R.id.home->viewpager.currentItem=0
                R.id.profile->viewpager.currentItem=1
                R.id.help->viewpager.currentItem=2
                R.id.settings->viewpager.currentItem=3
            }
            true
        }

        viewpager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> navview.selectedItemId = R.id.home
                    1 -> navview.selectedItemId = R.id.profile
                    2 -> navview.selectedItemId = R.id.help
                    3 -> navview.selectedItemId = R.id.settings
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