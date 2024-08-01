package com.example.groot.model

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.groot.fragments.HelpFragment
import com.example.groot.fragments.HomeFragment
import com.example.groot.fragments.ProfileFragment
import com.example.groot.fragments.SettingsFragment

class ViewPagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ProfileFragment()
            2 -> HelpFragment()
            3 -> SettingsFragment()
            else -> HomeFragment()
        }
    }

}