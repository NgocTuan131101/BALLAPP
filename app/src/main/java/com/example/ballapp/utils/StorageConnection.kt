package com.example.ballapp.utils

import com.google.firebase.storage.FirebaseStorage

object StorageConnection {
    val storageReference = FirebaseStorage.getInstance()
}