package com.example.ballapp.main.FragmentMessagen.MessagenDetails

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballapp.main.FragmentMessagen.ChatViewModel
import com.example.ballapp.model.ChatModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ChatDetailsRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun saveChat(
        senderId: String,
        receiverId: String,
        message: String,
        time: String,
        teamAvatar: String,
        teamName: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,
    ) {
        val chatData = ChatModel(senderId, receiverId, message, time, teamAvatar, teamName)
        firebaseDatabase.getReference("Chat").push().setValue(chatData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.toString())
            }
    }

    fun readMessage(
        senderId: String,
        receiverId: String,
        onSuccess: (ArrayList<ChatModel>) -> Unit,
        onFail: (String) -> Unit,
    ) {
        firebaseDatabase.getReference("Chat").addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listRequest = ArrayList<ChatModel>()
                    for (requestSnapshot in snapshot.children) {
                        val chatDetails = requestSnapshot.getValue(ChatModel::class.java)
                        if (chatDetails!!.senderId.equals(senderId) && chatDetails.receiverId.equals(
                                receiverId
                            ) ||
                            chatDetails!!.senderId.equals(receiverId) && chatDetails.receiverId.equals(
                                senderId
                            )
                        ) {
                            listRequest.add(chatDetails)
                        }
                    }
                    onSuccess(listRequest)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onFail(error.message)
            }
        })
    }
}