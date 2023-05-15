package com.example.ballapp.main.FragmentMessagen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.R
import com.example.ballapp.adapter.ChatAdapter
import com.example.ballapp.databinding.FragmentChatBinding
import com.example.ballapp.`interface`.OnIconClickListerner
import com.example.ballapp.main.FragmentMessagen.MessagenDetails.ChatDetailsActivity
import com.example.ballapp.model.UsersModel
import com.example.ballapp.utils.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var chatBinding: FragmentChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if(userUID != null){
            chatViewModel.loadChatList(userUID)
        }
    }

    private fun initObserve() {
        chatViewModel.loadChatList.observe(viewLifecycleOwner) { result ->
            with(chatBinding) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is ChatViewModel.LoadChatList.Loading -> {
                    chatBinding.progressBar.visibility = View.VISIBLE
                }
                is ChatViewModel.LoadChatList.ResultOk -> {
                    chatAdapter.addNewData(result.list)
                    intiSearch()
                }
                is ChatViewModel.LoadChatList.ResultError -> {}
            }
        }
    }

    private fun intiSearch() {
        chatBinding.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
        })
    }

    private fun filter(text: String) {
        DatabaseConnection.databaseReference.getReference("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val filteredList = ArrayList<UsersModel>()
                        for (requestSnapshot in snapshot.children) {
                            requestSnapshot.getValue(UsersModel::class.java)?.let { list ->
                                when {
                                    list.teamName.lowercase(Locale.getDefault())
                                        .contains(text.lowercase(Locale.getDefault()))
                                            && userUID != list.userUid -> {
                                        filteredList.add(0, list)
                                    }
                                }
                            }
                        }
                        chatAdapter.addFilterList(filteredList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    private fun initList() {
        chatBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            chatAdapter = ChatAdapter(arrayListOf())
            adapter = chatAdapter
            chatAdapter.OnIconClickListerner(object : OnIconClickListerner {
                override fun onIconClick(requestData: UsersModel) {
                    ChatDetailsActivity.startDetails(context, requestData)
                    activity?.overridePendingTransition(
                        R.anim.animate_slide_left_enter,
                        R.anim.animate_slide_left_exit
                    )

                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        chatBinding = FragmentChatBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return chatBinding.root
    }
}