package com.example.ballapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ballapp.home.all.AllFragmentMainActivity
import com.example.ballapp.home.nearme.NearMeFragmentMainActivity
import com.example.ballapp.home.today.TodayFragmentMainActivity
import com.example.ballapp.main.newcreatmatch.NewCreateFragment
import javax.inject.Inject

class HomePagerAdapter  @Inject constructor(fm : FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
        override fun getItemCount(): Int {
            return 4
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 ->{
                    AllFragmentMainActivity()
                }
                1-> {
                    TodayFragmentMainActivity()
                }
                2 ->{
                    NearMeFragmentMainActivity()
                }
                3 ->{
                    NewCreateFragment()
                }

                else -> {
                    Fragment()
                }
            }
        }
    }