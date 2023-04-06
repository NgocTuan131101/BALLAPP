package com.example.ballapp.utils

import com.google.firebase.auth.FirebaseAuth

object AuthConnection {
    val auth = FirebaseAuth.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
}