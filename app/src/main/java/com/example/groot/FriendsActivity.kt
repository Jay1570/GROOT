package com.example.groot

import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.viewpager2.widget.ViewPager2
import com.example.groot.adapter.FriendsViewPagerAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FriendsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var appBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_friends)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        appBar = findViewById(R.id.topAppBar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val orientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val bar = v.findViewById<MaterialToolbar>(R.id.topAppBar)
            val layoutParams = bar.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin,
                if (orientation) layoutParams.topMargin else systemBarsInsets.top,
                systemBarsInsets.right,
                layoutParams.bottomMargin
            )
            bar.layoutParams = layoutParams
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(bottom = imeInsets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }

        val currentItem = intent.getIntExtra("position", 0)
        viewPager.adapter = FriendsViewPagerAdapter(this)

        viewPager.currentItem = currentItem

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Followers"
                1 -> "Following"
                else -> "Unknown"
            }
        }.attach()

        appBar.setNavigationOnClickListener {
            finish()
        }
    }
}