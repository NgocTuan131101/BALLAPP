package com.example.ballapp.main.FragmentMessagen.MessagenDetails

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballapp.adapter.ChatAdapter
import com.example.ballapp.adapter.ChatDetailsAdapter
import com.example.ballapp.databinding.ActivityChatDetailsBinding
import com.example.ballapp.model.UsersModel
import com.example.ballapp.utils.Model.receiverId
import com.example.ballapp.utils.Model.teamImageUrl
import com.example.ballapp.utils.Model.teamName
import com.example.ballapp.utils.StorageConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ChatDetailsActivity : AppCompatActivity() {
    private lateinit var activityChatDetailsBinding: ActivityChatDetailsBinding
    private val chatDetailsViewModel: ChatDetailsViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var chatDetailsAdapter: ChatDetailsAdapter
    var thisTeamName: String? = null
    private var intentReceiverId: String? = null

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data: UsersModel?) {
            context.startActivity(Intent(context, ChatDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatDetailsBinding = ActivityChatDetailsBinding.inflate(layoutInflater)
        setContentView(activityChatDetailsBinding.root)
        initEvents()
        initoObserver()
        if (userUID != null) {
            if (receiverId.isNullOrEmpty()) {
                chatDetailsViewModel.readMessage(userUID, intentReceiverId!!)
            } else {
                chatDetailsViewModel.readMessage(userUID, receiverId!!)
            }
        }
    }

    private fun initoObserver() {
        saveChatObserve()
        readMessageObserve()
    }

    private fun readMessageObserve() {
        chatDetailsViewModel.readMessageResult.observe(this){result ->
            when(result){
                is ChatDetailsViewModel.ReadMessageResult.Loading ->{}
                is ChatDetailsViewModel.ReadMessageResult.ResultOk ->{
                    chatDetailsAdapter.addNewData(result.list)
                }
                is ChatDetailsViewModel.ReadMessageResult.ResultError ->{}
                else -> {}
            }
        }
    }

    private fun saveChatObserve() {
        chatDetailsViewModel.saveChatResult.observe(this){result ->
            when(result){
                is ChatDetailsViewModel.SaveChatResult.ResultOk -> {}
                is ChatDetailsViewModel.SaveChatResult.ResultError -> {}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        binding()
        intentBinding()
        sendChat()
        back()
        initListMessage()
        handleVariable()
    }

    private fun handleVariable() {
        if (userUID != null) {
            FirebaseDatabase.getInstance().getReference("Teams").child(userUID).get()
                .addOnSuccessListener {
                    teamName = it.child("teamName").value.toString()
            }
        }
        if(userUID != null ){
            StorageConnection.storageReference.getReference("Users").child(userUID).downloadUrl
                .addOnSuccessListener {
                    teamImageUrl = it.toString()
                }
                .addOnFailureListener {
                    Log.e("Error",it.toString())
                }
        }
    }

    private fun initListMessage() {
        activityChatDetailsBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            chatDetailsAdapter = ChatDetailsAdapter(arrayListOf())
            adapter = chatDetailsAdapter

        }
    }

    private fun back() {
        activityChatDetailsBinding.back.setOnClickListener {
            finish()
            com.example.ballapp.utils.Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        com.example.ballapp.utils.Animation.animateSlideRight(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendChat() {
        activityChatDetailsBinding.send.setOnClickListener {
            if (activityChatDetailsBinding.message.text.isNotEmpty()) {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
                val formatted = current.format(formatter).toString()
                if (userUID != null) {
                    if (receiverId == null) {
                        chatDetailsViewModel.saveChat(
                            userUID,
                            intentReceiverId!!,
                            activityChatDetailsBinding.message.text.toString(),
                            formatted,
                            teamImageUrl!!,
                            teamName!!
                        )
                    } else {
                        chatDetailsViewModel.saveChat(
                            userUID,
                            receiverId!!,
                            activityChatDetailsBinding.message.text.toString(),
                            formatted,
                            teamImageUrl!!,
                            teamName!!
                        )
                    }
                }
                activityChatDetailsBinding.message.text.clear()
            } else {
                Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun intentBinding() {
        val name = intent?.getStringExtra("teamName")
        val userUid = intent?.getStringExtra("userUid")
        if (name.isNullOrEmpty()) {
            activityChatDetailsBinding.teamName.text = thisTeamName
        } else {
            activityChatDetailsBinding.teamName.text = name
            intentReceiverId = userUid
        }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<UsersModel>(KEY_DATA)
            with(activityChatDetailsBinding) {
                thisTeamName = data?.teamName
                teamName.text = data?.teamName
                receiverId = data?.userUid
            }
        }
    }
}