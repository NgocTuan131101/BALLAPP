package com.example.ballapp.main

import com.google.firebase.database.FirebaseDatabase
import dagger.assisted.AssistedInject
import javax.inject.Inject

class MainActivityRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun updateUser(
        userUID: String,
        avatarUrl: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,
    ) {
        val updateUsers = mapOf(
            "avatarUrl" to avatarUrl
        )
        firebaseDatabase.getReference("Users").child(userUID).updateChildren(updateUsers)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener() {
                onFail(it.message.orEmpty())
            }
    }

    fun updateTeam(
        userUID: String,
        teamImageUrl: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,
    ) {
        val updateTeams = mapOf(
            "teamImageUrl" to teamImageUrl
        )
        firebaseDatabase.getReference("Teams").child(userUID).updateChildren(updateTeams)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }
}