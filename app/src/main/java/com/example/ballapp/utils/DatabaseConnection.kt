package com.example.ballapp.utils

import com.google.firebase.database.FirebaseDatabase

object DatabaseConnection {
    val databaseReference = FirebaseDatabase.getInstance()
}