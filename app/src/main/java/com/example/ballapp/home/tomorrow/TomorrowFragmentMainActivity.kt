package com.example.ballapp.home.tomorrow

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
import com.example.ballapp.home.all.AllDetailsActivity.MainActivityAllDetails
import com.example.ballapp.adapter.HomePagerAdapter
import com.example.ballapp.databinding.FragmentNearmeMainActivityBinding
import com.example.ballapp.databinding.FragmentTomorrowMainActivityBinding
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TomorrowFragmentMainActivity : Fragment() {
    private lateinit var tomorrowFrament: FragmentTomorrowMainActivityBinding
    private lateinit var tomorrowAdapter: HomeAdapter
    private val tomorrowFragmentViewModel:TomorrowFragmentViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initOb()
    }

    private fun initOb() {
        tomorrowFragmentViewModel.loadTomorrowList.observe(viewLifecycleOwner){result ->
            with(tomorrowFrament){
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when(result){
               is TomorrowFragmentViewModel.LoadTomorrowList.Loading -> {
                   tomorrowFrament.progressBar.visibility =View.VISIBLE
               }
                is TomorrowFragmentViewModel.LoadTomorrowList.ResultOk ->{
                    if(result.list.isEmpty()){
                        tomorrowFrament.recyclerView.visibility = View.GONE
                        tomorrowFrament.imageLayout.visibility = View.VISIBLE
                        tomorrowFrament.progressBar.visibility = View.GONE
                    }
                    else{
                        tomorrowAdapter.addNewData(result.list)
                    }
                }
                is TomorrowFragmentViewModel.LoadTomorrowList.ResultError ->{
                    Toast.makeText(context,result.errorMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initList() {
        tomorrowFrament.recyclerView.apply {
            layoutManager =LinearLayoutManager(context)
            tomorrowAdapter =HomeAdapter(arrayListOf())
            adapter =tomorrowAdapter
            tomorrowAdapter.setOnItemClickListerner(object :OnItemClickListerner{
                override fun onItemClick(requestData: CreateMatchModel) {
                    MainActivityAllDetails.startDetails(context,requestData)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        tomorrowFrament = FragmentTomorrowMainActivityBinding.inflate(inflater,container,false)

        return tomorrowFrament.root
    }
}