package com.example.ballapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ballapp.main.FragmentBattle.newcreata.NewCreateFragment
import com.example.ballapp.main.Fragmentmatch.confirm.ConfirmFragment
import com.example.ballapp.main.Fragmentmatch.upcoming.UpComingFragment
import com.example.ballapp.main.Fragmentmatch.wait.WaitFragment

import javax.inject.Inject

class MatchPagerAdapter @Inject constructor(fm : FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0->{
                UpComingFragment()
            }
            1->{
                ConfirmFragment()
            }
            2->{
                WaitFragment()
            }
            3->{
                NewCreateFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}