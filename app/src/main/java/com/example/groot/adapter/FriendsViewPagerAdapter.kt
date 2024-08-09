package com.example.groot.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.groot.fragments.FollowersFragment
import com.example.groot.fragments.FollowingFragment

class FriendsViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FollowersFragment()
            1 -> FollowingFragment()
            else -> throw IllegalArgumentException("Invalid position :- $position")
        }
    }
}