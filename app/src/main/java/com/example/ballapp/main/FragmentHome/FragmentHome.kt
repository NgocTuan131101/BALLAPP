package com.example.ballapp.main.FragmentHome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.ballapp.R
import com.example.ballapp.UserAndAnnouncementAndSearch.User.MainActivityUser
import com.example.ballapp.adapter.HomePagerAdapter
import com.example.ballapp.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FragmentHome : Fragment() {

    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val localFide = File.createTempFile("TempImage", "jpg")
    private val fragmentHomeViewModel: FragmentHomeViewModel by viewModels()

    @SuppressLint("UnsafeOptInUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        initEvents()
        initObserve()
        if (userUID != null) {
            fragmentHomeViewModel.loadAvatar(userUID, localFide)
        }
    }

//    @Override
//    override fun onResume() {
//        Log.e("DEBUG", "onResume of LoginFragment");
//        super.onResume()
//    }
//    @Override
//    override fun onPause() {
//        Log.e("DEBUG", "OnPause of loginFragment");
//        super.onPause();
//    }

    private fun initObserve() {
        fragmentHomeViewModel.loadAvatar.observe(viewLifecycleOwner) { result ->
            when (result) {
                is FragmentHomeViewModel.LoadAvatar.ResultOK -> {
                    fragmentHomeBinding.userAvatar.setImageBitmap(result.image)
                }
                is FragmentHomeViewModel.LoadAvatar.ResultError -> {
                }
            }
        }
    }

    private fun initEvents() {
        userInfo()
    }

    private fun userInfo() {
        fragmentHomeBinding.userAvatar.setOnClickListener {
            startActivity(Intent(context, MainActivityUser::class.java))
            activity?.overridePendingTransition(
                R.anim.animate_slide_in_left,
                R.anim.animate_slide_out_right
            )
        }
    }

    private fun initViewPager() {
        val matPagerAdapter = HomePagerAdapter(childFragmentManager, lifecycle)
        fragmentHomeBinding.viewPager.adapter = matPagerAdapter
        TabLayoutMediator(
            fragmentHomeBinding.tabLayout,
            fragmentHomeBinding.viewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Tất cả "
                }
                1 -> {
                    tab.text = "Hôm nay "
                }
                2 -> {
                    tab.text = "Ngày mai"
                }
                3 -> {
                    tab.text = "Gần tôi"
                }
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return fragmentHomeBinding.root
    }
}