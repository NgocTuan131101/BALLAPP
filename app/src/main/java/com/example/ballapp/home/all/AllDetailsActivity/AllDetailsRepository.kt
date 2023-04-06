package com.example.ballapp.home.all.AllDetailsActivity

import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class AllDetailsRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    @Exclude
    fun catchMatch(
        matchId : String,
        uid: String,
        clientUid : String, // khách hàng Uid
        click : Int,
        onSuccess :(String) -> Unit,
        onFail :(String) -> Unit

    ){
        val user = mapOf(
            clientUid to uid,
            "click" to click
        )
        firebaseDatabase.getReference("Request_Match").child(matchId).updateChildren(user)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    onSuccess(it.toString())
                }
                else{
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
             }

    }
}