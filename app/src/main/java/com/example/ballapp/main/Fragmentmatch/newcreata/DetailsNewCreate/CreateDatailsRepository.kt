package com.example.ballapp.main.Fragmentmatch.newcreata.DetailsNewCreate

import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class CreateDatailsRepository @Inject constructor(private val  firebaseDatabase: FirebaseDatabase){
    fun deteteCreateDatails (
        userUID :String,
        mathID :String,
        onSuccsess:(String) ->Unit,
        onFail :(String) -> Unit
    ){
        firebaseDatabase.getReference("Request_Match").child(mathID).removeValue()
        firebaseDatabase.getReference("New_Create_Match").child(userUID).child(mathID).removeValue()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    onSuccsess(it.toString())
                }else{
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }

}