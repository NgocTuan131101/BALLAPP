//package com.example.ballapp.sharedPreferences
//
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import javax.inject.Inject
//
//class SharedPreferencesRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
//     fun SharedUser(
//         userUID: String,
//         onSuccess: (DataSnapshot) -> Unit,
//         onFail: (Exception) -> Unit,
//     ){
//         firebaseDatabase.getReference("Users").child(userUID).get()
//             .addOnSuccessListener {
//                 onSuccess(it)
//             }
//             .addOnFailureListener {
//                 onFail(it)
//             }
//
//     }
//}