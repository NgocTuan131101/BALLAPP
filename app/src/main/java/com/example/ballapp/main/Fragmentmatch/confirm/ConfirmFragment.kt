package com.example.ballapp.main.Fragmentmatch.confirm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.R
import com.example.ballapp.adapter.ConfirmAdapter
import com.example.ballapp.databinding.FragmentConfirmBinding
import com.example.ballapp.`interface`.HighLightOnClickListerner
import com.example.ballapp.`interface`.NotHighLightOnClickListerner
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballapp.main.Fragmentmatch.confirm.DetailsConfirm.ActivityConfirmDetails
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmFragment : Fragment() {
    private lateinit var confirmBinding: FragmentConfirmBinding
    private lateinit var confirmAdapter: ConfirmAdapter
    private val confirmViewModel: ConfirmViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        loadListObserve()
        if (userUID != null) {
            confirmViewModel.loadConfirmList(userUID)
        }
    }


    private fun loadListObserve() {
        confirmViewModel.loadConfirm.observe(viewLifecycleOwner) { result ->
            with(confirmBinding) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is ConfirmViewModel.LoadConfirmResult.Loading -> {
                    confirmBinding.progressBar.visibility = View.VISIBLE
                }
                is ConfirmViewModel.LoadConfirmResult.ResultOk -> {
                    if (result.list.isEmpty()) {
                        confirmBinding.imageLayout.visibility = View.VISIBLE
                        confirmBinding.recyclerView.visibility = View.GONE
                        confirmBinding.progressBar.visibility = View.GONE
                    } else {
                        confirmAdapter.addNewData(result.list)
                    }
                }
                is ConfirmViewModel.LoadConfirmResult.ResultError -> {}
                else -> {}
            }
        }
    }

    private fun initList() {
        confirmBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            confirmAdapter = ConfirmAdapter(arrayListOf())
            adapter = confirmAdapter

            confirmAdapter.setOnItemClickListerner(object :
                OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    ActivityConfirmDetails.startDetails(context,requestData)
                    activity?.overridePendingTransition(R.anim.animate_fade_enter,R.anim.animate_slide_left_exit)
                }
            })

            confirmAdapter.setOnHighLightClickListerner(object :
                HighLightOnClickListerner {
                override fun onHighLightClickListerner(requestData: CreateMatchModel) {
                    confirmViewModel.handleHighLight(userUID!!, requestData.matchID)
                }
            })
            confirmAdapter.setOnNotHighLightClickListerner(object : NotHighLightOnClickListerner {
                override fun onNotHighLightClickListerner(requestData: CreateMatchModel) {
                    confirmViewModel.handleNotHighLight(
                        userUID!!, requestData.matchID
                    )
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        confirmBinding = FragmentConfirmBinding.inflate(inflater, container, false)
        return confirmBinding.root
    }
}