package com.example.ballapp.main.Fragmentmatch.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.R
import com.example.ballapp.adapter.UpComingAdapter
import com.example.ballapp.databinding.FragmentUpComingBinding
import com.example.ballapp.`interface`.HighLightOnClickListerner
import com.example.ballapp.`interface`.NotHighLightOnClickListerner
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballapp.main.Fragmentmatch.upcoming.DetailsUpcoming.ActivityUpcomingDetails
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpComingFragment : Fragment() {
    private lateinit var upComingBinding: FragmentUpComingBinding
    private lateinit var upComingAdapter: UpComingAdapter
    private val upComingViewModel: UpComingViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        loadListOb()
    }

    private fun loadListOb() {
        upComingViewModel.loadUpComing.observe(viewLifecycleOwner){result ->
            with(upComingBinding){
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when(result){
                is UpComingViewModel.LoadUpComingResult.Loading ->{
                    upComingBinding.progressBar.visibility = View.VISIBLE
                }
                is UpComingViewModel.LoadUpComingResult.ResultOk ->{
                    if(result.list.isEmpty()){
                        upComingBinding.imageLayout.visibility = View.VISIBLE
                        upComingBinding.recyclerView.visibility = View.GONE
                        upComingBinding.progressBar.visibility =View.GONE
                    }
                    else{
                        upComingAdapter.addNewData(result.list)
                    }
                }
                is UpComingViewModel.LoadUpComingResult.ResultError -> {}
            }
        }
    }

    private fun initList() {
        upComingBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            upComingAdapter = UpComingAdapter(arrayListOf())
            adapter =upComingAdapter

            upComingAdapter.setOnItemClickListerner(object :
            OnItemClickListerner{
                override fun onItemClick(requestData: CreateMatchModel) {
                    ActivityUpcomingDetails.startDetails(context,requestData)
                    activity?.overridePendingTransition(R.anim.animate_slide_left_enter,R.anim.animate_slide_left_exit)

                }
            })
            upComingAdapter.setOnHighLightClickListerner(object :
            HighLightOnClickListerner{
                override fun onHighLightClickListerner(requestData: CreateMatchModel) {
                    upComingViewModel.handleNotHighLight(userUID!!,requestData.matchID)
                }
            })
            upComingAdapter.setOnNotHighLightClickListerner(object :
            NotHighLightOnClickListerner{
                override fun onNotHighLightClickListerner(requestData: CreateMatchModel) {
                    upComingViewModel.handleNotHighLight(userUID!!,requestData.matchID)
                }
            })

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        upComingBinding = FragmentUpComingBinding.inflate(inflater,container,false)
        return upComingBinding.root
    }

}