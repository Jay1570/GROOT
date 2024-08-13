package com.example.groot.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.groot.fragments.ExploreFragment
import com.example.groot.fragments.HomeFragment
import com.example.groot.fragments.ProfileFragment

class HomeViewPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment()
            1 -> ExploreFragment()
            2 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid position :- $position")
        }
    }
}