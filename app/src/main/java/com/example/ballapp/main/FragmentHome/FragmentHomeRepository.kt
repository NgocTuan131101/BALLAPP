package com.example.ballapp.main.FragmentHome

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import javax.inject.Inject

class FragmentHomeRepository @Inject constructor(private val firebaseStorage: FirebaseStorage) {
fun loadAvatar(
        userUID: String,
        loadFile :File,
        onFail :(Exception) ->Unit,
        onSuppress: (Bitmap) ->Unit
    ){
        firebaseStorage.getReference("Users").child(userUID).getFile(loadFile)
            .addOnSuccessListener {
                val bitmapOptions = BitmapFactory.Options()
                val bitmap = BitmapFactory.decodeFile(loadFile.absolutePath,bitmapOptions)
                onSuppress(bitmap)

            }
            .addOnFailureListener{
                onFail(it)
            }
    }
}