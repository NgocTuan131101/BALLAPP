package com.example.ballapp.home.all

import android.content.ContentValues.TAG
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.R
import com.example.ballapp.adapter.HomeAdapter
import com.example.ballapp.databinding.FragmentAllBinding
import com.example.ballapp.home.all.AllDetailsActivity.MainActivityAllDetails
import com.example.ballapp.`interface`.OnItemClickListerner
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllFragmentMainActivity : Fragment() {
    private lateinit var allFragmentAllBinding: FragmentAllBinding
    private val allViewModelFragment: AllViewModelFragment by viewModels()
    private lateinit var homeAdapter: HomeAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        loadListObserve()
        highLightObserve()
        if (userUID != null) {
            allViewModelFragment.loadAll(userUID)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        allFragmentAllBinding = FragmentAllBinding.inflate(inflater, container, false)
        return allFragmentAllBinding.root
    }
    /*
        hàm highLightObserve() này chịu trách nhiệm xử lý các sự kiện được gửi từ ViewModel allViewModelFragment
        thông qua LiveData object highLight. Việc xử lý sự kiện cụ thể được thực hiện
        trong khối mã của từng trường hợp trong hàm when().
    */
    private fun highLightObserve() {
        allViewModelFragment.highLight.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AllViewModelFragment.HighLighResult.NotHighligeOk -> {}
                is AllViewModelFragment.HighLighResult.NotHighligeErorr -> {}
                is AllViewModelFragment.HighLighResult.HighligeErorr -> {}
                is AllViewModelFragment.HighLighResult.HighligeOk -> {}
            }
        }
    }
    /*
         loadListObserve() này chịu trách nhiệm xử lý các sự kiện được gửi từ
          ViewModel allViewModelFragment thông qua LiveData object loadALLlist.
          Việc xử lý sự kiện cụ thể được thực hiện trong khối
           mã của từng trường hợp trong hàm when().
    */
    private fun loadListObserve() {
        allViewModelFragment.loadALLlist.observe(viewLifecycleOwner) { result ->
            with(allFragmentAllBinding) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {

                is AllViewModelFragment.LoadAllList.Loading -> {
                    allFragmentAllBinding.progressBar.visibility = View.VISIBLE
                }
                is AllViewModelFragment.LoadAllList.ResultOK -> {
                    if (result.list.isEmpty()) {

                        allFragmentAllBinding.recyclerView.visibility = View.GONE
                        allFragmentAllBinding.imageLayout.visibility = View.VISIBLE
                        allFragmentAllBinding.progressBar.visibility = View.GONE
                    } else {
                        homeAdapter.addNewData(result.list)
                    }
                }
                is AllViewModelFragment.LoadAllList.ResultError -> {
                    Toast.makeText(context, result.errerMessage, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
    private fun initList() {
        allFragmentAllBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            homeAdapter = HomeAdapter(arrayListOf())
            adapter = homeAdapter
            homeAdapter.setOnItemClickListerner(object : OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    MainActivityAllDetails.startDetails(context, requestData)
                    activity?.overridePendingTransition(
                        R.anim.animate_slide_left_enter,
                        R.anim.animate_slide_left_exit
                    )
                }
            })
        }
    }
}