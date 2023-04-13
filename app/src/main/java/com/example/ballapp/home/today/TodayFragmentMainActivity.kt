package com.example.ballapp.home.today

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.R
import com.example.ballapp.adapter.HomeAdapter
import com.example.ballapp.adapter.NewCreateAdapter
import com.example.ballapp.databinding.FragmentTodayMainActivityBinding
import com.example.ballapp.home.all.AllDetailsActivity.MainActivityAllDetails
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodayFragmentMainActivity : Fragment() {
    private lateinit var todaybinding: FragmentTodayMainActivityBinding
    private val todayFragmentViewModel:TodayFragmentViewModel by viewModels()
    private lateinit var todayAdapter: HomeAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if(userUID != null){
            todayFragmentViewModel.loadToday(userUID)
        }
    }

    private fun initObserve() {
        todayFragmentViewModel.loadTodayList.observe(viewLifecycleOwner){result->
            with(todaybinding){
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when(result){
                is TodayFragmentViewModel.LoadTodayList.Loading->{
                    todaybinding.progressBar.visibility = View.VISIBLE
                }
                is TodayFragmentViewModel.LoadTodayList.ResultOk->{
                    if(result.list.isEmpty()){
                        todaybinding.recyclerView.visibility = View.GONE
                        todaybinding.progressBar.visibility = View.GONE
                        todaybinding.imageLayout.visibility = View.VISIBLE
                    }
                    else{
                        todayAdapter.addNewData(result.list)
                    }
                }
                is  TodayFragmentViewModel.LoadTodayList.ResultError->{
                    Toast.makeText(context,result.errorMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun initList() {
        todaybinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            todayAdapter = HomeAdapter(arrayListOf())
            adapter =todayAdapter
            todayAdapter.setOnItemClickListerner(object :OnItemClickListerner{
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
        // Inflate the layout for this fragment
        return todaybinding.root
    }
}