package com.example.groot.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.groot.fragments.HelpFragment
import com.example.groot.fragments.HomeFragment
import com.example.groot.fragments.ProfileFragment
import com.example.groot.fragments.SettingsFragment

class ViewPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment()
            1 -> ProfileFragment()
            2 -> HelpFragment()
            3 -> SettingsFragment()
            else -> throw IllegalArgumentException("Invalid position :- $position")
        }
    }
}