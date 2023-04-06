package com.example.ballapp.UserAndAnnouncementAndSearch.User

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

/*
    truy xuất tới dữ liệu người dùng từ cơ sở dữ liệu firebase Realtime Database
    loadPhoneAndName : tải thông tin người dùng( tên and phone ) dựa trên userUID
 */
class UserRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun loadPhoneAndName(
        userUID: String,
        onSuccess: (DataSnapshot) -> Unit,
        onFail: (Exception) -> Unit,
    ) {
        firebaseDatabase.getReference("Users").child(userUID).get()
            .addOnSuccessListener {
                onSuccess(it)
            }
            .addOnFailureListener {
                onFail(it)
            }
    }
}