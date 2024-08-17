package com.example.groot

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
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
        viewPager=findViewById(R.id.viewPager)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        window.navigationBarColor = getColor(R.color.md_theme_surfaceContainer)

        viewPager.adapter=HomeViewPagerAdapter(this)
        navView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home->viewPager.currentItem=0
                R.id.explore->viewPager.currentItem=1
                R.id.profile->viewPager.currentItem=2
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> navView.selectedItemId = R.id.home
                    1 -> navView.selectedItemId = R.id.explore
                    2 -> navView.selectedItemId = R.id.profile
                }
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomPadding = if (!insets.isVisible(WindowInsetsCompat.Type.ime())) systemBarsInsets.bottom else 0
            v.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                bottomPadding
            )
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(bottom = imeInsets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }
    }
}