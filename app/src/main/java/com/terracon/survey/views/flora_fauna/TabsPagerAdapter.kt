package com.terracon.survey.views.flora_fauna

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagerAdapter(fa: FragmentActivity,
                      private var tabCount: Int): FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                // # Flora Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "Flora")
                val musicFragment = ListFragment()
                musicFragment.arguments = bundle
                return musicFragment
            }
            1 -> {
                // # Fauna Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "Fauna")
                val moviesFragment = ListFragment()
                moviesFragment.arguments = bundle
                return moviesFragment
            }
            else -> return ListFragment()
        }
    }

    override fun getItemCount(): Int {
        return tabCount
    }
}
