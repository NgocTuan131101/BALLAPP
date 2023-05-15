package com.example.ballapp.home.nearme

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.R
import com.example.ballapp.adapter.NearMeAdapter
import com.example.ballapp.databinding.FragmentNearmeMainActivityBinding
import com.example.ballapp.home.all.AllDetailsActivity.MainActivityAllDetails
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballball.model.CreateMatchModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class NearMeFragmentMainActivity : Fragment() {
    private lateinit var nearMeFragment: FragmentNearmeMainActivityBinding
    private lateinit var nearMeAdapter: NearMeAdapter
    private val nearMeFragmentViewModel:NearMeFragmentViewModel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocation()
        initOb()
        initList()
    }
    private fun getLocation(){
        fusedLocationProviderClient = activity?.let {
            LocationServices.getFusedLocationProviderClient(it.applicationContext)
        }!!
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location :Location? ->
            if(location != null){
                val currentLat = location.latitude
                val currentLong = location.longitude
                Log.e("currentLat", currentLat.toString())
                Log.e("currentLong", currentLong.toString())
                if(userUID != null){
                    nearMeFragmentViewModel.loadNearMeList(userUID,currentLat,currentLong)
                }
            }
        }
    }
    private fun initOb() {
        nearMeFragmentViewModel.loadNearMe.observe(viewLifecycleOwner) { result ->
            with(nearMeFragment) {
                progressBar.visibility = View.GONE
                nearMeRecyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is NearMeFragmentViewModel.LoadNearMeResult.Loading -> {
                    nearMeFragment.progressBar.visibility = View.VISIBLE
                }
                is NearMeFragmentViewModel.LoadNearMeResult.ResultOk -> {
                    Log.e("Size", result.list.size.toString())
                    if (result.list.isEmpty()) {
                        nearMeFragment.nearMeRecyclerView.visibility = View.GONE
                        nearMeFragment.imageLayout.visibility = View.VISIBLE
                        nearMeFragment.progressBar.visibility = View.GONE
                    } else {
                        nearMeAdapter.addNewData(result.list)
                    }
                }
                is NearMeFragmentViewModel.LoadNearMeResult.ResultError -> {}
            }
        }
    }

    private fun initList(){
        nearMeFragment.nearMeRecyclerView.apply{
            layoutManager = LinearLayoutManager(context)
            nearMeAdapter = NearMeAdapter(arrayListOf())
            adapter = nearMeAdapter
            nearMeAdapter.setOnItemClickListerner(object :
            OnItemClickListerner{
                override fun onItemClick(requestData: CreateMatchModel) {
                    MainActivityAllDetails.startDetails(context,requestData)
                    activity?.overridePendingTransition(R.anim.animate_slide_left_enter,R.anim.animate_slide_left_exit)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        nearMeFragment = FragmentNearmeMainActivityBinding.inflate(inflater,container,false)
        return nearMeFragment.root
    }
}