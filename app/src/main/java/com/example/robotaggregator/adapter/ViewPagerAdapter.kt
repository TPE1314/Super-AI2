package com.example.robotaggregator.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.robotaggregator.ui.ChatFragment
import com.example.robotaggregator.ui.RobotListFragment
import com.example.robotaggregator.ui.SettingsFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    
    override fun getItemCount(): Int = 3
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RobotListFragment()
            1 -> ChatFragment()
            2 -> SettingsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}