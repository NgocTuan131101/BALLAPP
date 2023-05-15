package com.example.ballapp.main.Fragmentmatch.newcreata

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.R
import com.example.ballapp.adapter.NewCreateAdapter
import com.example.ballapp.creatMatch.ActivityCreatMatchNew
import com.example.ballapp.databinding.FragmentNewCreateBinding
import com.example.ballapp.home.all.AllDetailsActivity.MainActivityAllDetails
import com.example.ballapp.`interface`.HighLightOnClickListerner
import com.example.ballapp.`interface`.NotHighLightOnClickListerner
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCreateFragment : Fragment() {
    private lateinit var newCreateBinding: FragmentNewCreateBinding
    private lateinit var newCreateAdapter: NewCreateAdapter
    private  val newCreateFramentViewModel: NewCreateFramentViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initEvents()
        initObserve()
    }

    private fun initObserve() {
        loadNewCreateObserve()
        highligtObserve()
    }

    private fun highligtObserve() {
        newCreateFramentViewModel.highLigt.observe(viewLifecycleOwner){result ->
            when(result){
                is NewCreateFramentViewModel.HighLighResult.NotHighLighResultOK ->{}
                is NewCreateFramentViewModel.HighLighResult.NotHighLighResultError ->{}
                is NewCreateFramentViewModel.HighLighResult.HighLighResultOk ->{}
                is NewCreateFramentViewModel.HighLighResult.HighLighResultError ->{}
                else -> {}
            }
        }
    }

    private fun loadNewCreateObserve() {
        newCreateFramentViewModel.loadNewCreate.observe(viewLifecycleOwner){result ->
            with(newCreateBinding){
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is NewCreateFramentViewModel.LoadNewCreate.Loading -> {
                }
                is NewCreateFramentViewModel.LoadNewCreate.ResultOk ->{
                    if(result.list.isEmpty()){
                        newCreateBinding.imageLayout.visibility = View.VISIBLE
                        newCreateBinding.buttom.visibility = View.VISIBLE
                        newCreateBinding.recyclerView.visibility = View.GONE
                        newCreateBinding.progressBar.visibility = View.GONE
                    }else{
                        newCreateAdapter.addNewData(result.list)
                    }
                }
                is NewCreateFramentViewModel.LoadNewCreate.ResultError -> {}
                else -> {}
            }
        }
    }

    private fun initEvents() {
        createMatchNew()
    }

    private fun createMatchNew() {
        newCreateBinding.buttom.setOnClickListener {
            startActivity(Intent(context,ActivityCreatMatchNew::class.java))
            activity?.overridePendingTransition(R.anim.animate_slide_left_enter,R.anim.animate_slide_left_exit)
        }
    }

    private fun initList() {
        newCreateBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            newCreateAdapter = NewCreateAdapter(arrayListOf())
            adapter = newCreateAdapter

            newCreateAdapter.setOnItemClickListerner(object :
            OnItemClickListerner{
                override fun onItemClick(requestData: CreateMatchModel) {
                    MainActivityAllDetails.startDetails(context,requestData)
                    activity?.overridePendingTransition(R.anim.animate_slide_left_exit,R.anim.animate_slide_left_exit)
                }
            })
            newCreateAdapter.setOnHighLightClickListerner(object :HighLightOnClickListerner {
                override fun onHighLightClickListerner(requestData: CreateMatchModel) {
                    newCreateFramentViewModel.handleHighLight(userUID!!,requestData.matchID)
                }
            })
            newCreateAdapter.setOnNotHighLightClickListerner(object :NotHighLightOnClickListerner
            {
                override fun onNotHighLightClickListerner(requestData: CreateMatchModel) {
                    newCreateFramentViewModel.handleNotHingLight(userUID!!,requestData.matchID)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        newCreateBinding=FragmentNewCreateBinding.inflate(layoutInflater)
        return newCreateBinding.root
    }
}