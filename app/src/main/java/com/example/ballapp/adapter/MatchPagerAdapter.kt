package com.example.ballapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ballapp.main.FragmentContact.ContactFragment
import com.example.ballapp.main.FragmentMessagen.ChatFragment
import com.example.ballapp.main.Fragmentmatch.confirm.ConfirmFragment
import com.example.ballapp.main.Fragmentmatch.newcreata.NewCreateFragment
import com.example.ballapp.main.Fragmentmatch.upcoming.UpComingFragment
import com.example.ballapp.main.Fragmentmatch.wait.WaitFragment
import org.checkerframework.checker.units.qual.C
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
                WaitFragment()
            }
            2->{
                NewCreateFragment()
            }
            3->{
                ConfirmFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}