package com.example.groot

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.groot.fregment.HelpFragment
import com.example.groot.fregment.HomeFragment
import com.example.groot.fregment.ProfileFragment
import com.example.groot.fregment.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var navview: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        navview=findViewById(R.id.bottom_navigation)

        replace(HomeFragment())

        navview.setOnItemSelectedListener(){
            when(it.itemId){
                R.id.home->replace(HomeFragment())
                R.id.profile->replace(ProfileFragment())
                R.id.help->replace(HelpFragment())
                R.id.settings->replace(SettingsFragment())
            }
            true
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun replace(frgment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nevhost,frgment)
        fragmentTransaction.commit()
    }
}