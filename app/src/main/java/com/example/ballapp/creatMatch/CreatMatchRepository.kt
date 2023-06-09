package com.example.ballapp.creatMatch

import com.example.ballapp.model.AllNotificationModel
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class CreatMatchRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
) {
    // gửi yêu cầu
    fun sendRequest(
        userUID: String,
        matchID: String,
        deviceToken: String,
        teamName: String,
        teamPhone: String,
        date: String,
        time: String,
        location: String,
        note: String,
        teamPeopleNumber: String,
        teamImageUrl: String,
        locationAddress: String,
        lat: Double,
        long: Double,
        geoHash: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,

        ) {
        val requestData = CreateMatchModel(
            userUID,
            matchID,
            deviceToken,
            teamName,
            teamPhone,
            date,
            time,
            location,
            note,
            teamPeopleNumber,
            teamImageUrl,
            locationAddress,
            lat,
            long,
            0,
            "",
            "",
            "",
            "",
            geoHash
        )
        firebaseDatabase.getReference("Request_Match").child(matchID).setValue(requestData)
            .addOnCompleteListener() {
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

    fun notification(
        matchID: String,
        teamName: String,
        userUID: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,
    ) {
        val allNotification = AllNotificationModel(matchID, teamName, userUID)
        firebaseDatabase.getReference("Request_Match_Notification").child(matchID)
            .setValue(allNotification)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }.addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }

    fun saveNewCreate(
        userUID: String,
        matchID: String,
        deviceToken: String,// mã thông báo thiêt bị
        teamName: String,
        teamPhone: String,
        date: String,
        time: String,
        location: String,
        note: String,
        teamPeopleNumber: String,
        teamImageUrl: String,
        locationAddress: String,
        lat: Double,
        long: Double,
        geoHash: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit,

        ) {
        val upComingDate = CreateMatchModel(
            userUID,
            matchID,
            deviceToken,
            teamName,
            teamPhone,
            date,
            time,
            location,
            note,
            teamPeopleNumber,
            teamImageUrl,
            locationAddress,
            lat,
            long,
            0,
            "",
            "",
            "",
            "",
            geoHash
        )
        firebaseDatabase.getReference("New_Create_Match").child(matchID).setValue(upComingDate)
            .addOnCompleteListener() {
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