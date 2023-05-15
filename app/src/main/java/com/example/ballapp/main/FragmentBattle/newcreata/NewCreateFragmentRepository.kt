package com.example.ballapp.main.Fragmentmatch.newcreata

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class NewCreateFragmentRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) :
    ViewModel() {
    fun loadNewCreate(
        userUID: String,
        onSuccess: (ArrayList<CreateMatchModel>) -> Unit,
        onFail: (String) -> Unit,
    ) {
        firebaseDatabase.getReference("New_Create_Match").child(userUID)
            .addValueEventListener(object :
                ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val listRequet = ArrayList<CreateMatchModel>()
                        for (requestSnapshot in snapshot.children) {
                            requestSnapshot.getValue(CreateMatchModel::class.java)?.let { list ->
                                val current = LocalDate.now()
                                val matchTime = list.date
                                val formatter =
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
                                val date = LocalDate.parse(matchTime, formatter)
                                when {
                                    date >= current -> {
                                        listRequet.add(0, list)
                                    }
                                }
                            }
                        }
                        onSuccess(listRequet)
                    } else {
                        val listRequest = ArrayList<CreateMatchModel>()
                        onSuccess(listRequest)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onFail(error.message)
                }
            })
    }

    fun highlight(
        userUID: String,
        matchID: String,
        onFail: (String) -> Unit,
        onSuccess: (String) -> Unit,
    ) {
        val highlight = mapOf(
            "highlight" to 1
        )
        firebaseDatabase.getReference("New_Create_Match").child(userUID).child(matchID)
            .updateChildren(highlight)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail
                (it.message.orEmpty())
            }
    }
    fun noHighLight(
        userUID: String,
        matchID: String,
        onFail: (String) -> Unit,
        onSuccess: (String) -> Unit

    ){
        val noHighList = mapOf(
            "highligh" to 0
        )
        firebaseDatabase.getReference("New_Create_Match").child(userUID).child(matchID).updateChildren(noHighList)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    onSuccess(it.toString())
                }else{
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }

}