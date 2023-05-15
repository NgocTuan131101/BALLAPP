package com.example.ballapp.main.FragmentBattle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ballapp.adapter.MatchPagerAdapter
import com.example.ballapp.databinding.FragmentMatchBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchFragment : Fragment() {
    private lateinit var matchBinding: FragmentMatchBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        val matchPagerAdapter = MatchPagerAdapter(childFragmentManager, lifecycle)
        matchBinding.ViewPager2.adapter = matchPagerAdapter
        TabLayoutMediator(matchBinding.tabLayout, matchBinding.ViewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Sắp đá"
                }
                1 -> {
                    tab.text = "Đã yêu cầu "
                }
                2 -> {
                    tab.text = "Xác nhận"
                }
                3->{
                    tab.text = "Lịch sử"
                }
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        matchBinding = FragmentMatchBinding.inflate(inflater, container, false)
        return matchBinding.root
    }
}