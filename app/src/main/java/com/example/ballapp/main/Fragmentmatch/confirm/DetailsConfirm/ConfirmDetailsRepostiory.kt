package com.example.ballapp.main.Fragmentmatch.confirm.DetailsConfirm

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ballapp.model.AcceptMatchNotification
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

class ConfirmDetailsRepostiory @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun denyMatch(
        userUID : String,
        matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("confirmRequest").child(userUID).child(matchID).removeValue()
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

    fun upComingMatch (
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, confirmUID: String, clientUID: String, geoHash : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val upComingMatch = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
            clientTeamName, clientImageUrl, confirmUID, clientUID, geoHash)

        firebaseDatabase.getReference("upComingMatch").child(userUID).child(matchID).setValue(upComingMatch)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                }
                else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }

    fun upComingMatchClient(
        confirmUID : String, userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, clientUID: String, geoHash: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val upComingMatchClient = CreateMatchModel(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
            clientTeamName, clientImageUrl, confirmUID, clientUID, geoHash)

        firebaseDatabase.getReference("upComingMatch").child(confirmUID).child(matchID).setValue(upComingMatchClient)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                }
                else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }
    }

    fun deleteWaitMatch(
        confirmUID : String,
        matchID: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        firebaseDatabase.getReference("waitRequest").child(confirmUID).child(matchID).removeValue()
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

    fun deleteConfirmMatch(
        userUID: String,
        matchID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("confirmRequest").child(userUID).child(matchID).removeValue()
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

    fun deleteMatch(
        matchID : String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("Request_Match").child(matchID).removeValue()
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

    fun deleteNewCreate(
        userUID: String,
        matchID: String,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        firebaseDatabase.getReference("New_Create_Match").child(userUID).child(matchID).removeValue()
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

    fun acceptRequestNotification(
        clientUID: String, userUID: String, matchID: String, date: String, time: String, teamName: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val acceptMatchNotification = AcceptMatchNotification(clientUID, userUID, matchID, date, time, teamName)
        firebaseDatabase.getReference("acceptRequest_Notification").child(userUID).child(matchID).setValue(acceptMatchNotification)
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

    fun denyRequestNotification(
        clientUID: String, userUID: String, matchID: String, date: String, time: String, teamName: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val acceptMatchNotification = AcceptMatchNotification(clientUID, userUID, matchID, date, time, teamName)
        firebaseDatabase.getReference("denyRequest_Notification").child(userUID).child(matchID).setValue(acceptMatchNotification)
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

    fun denyRequestListNotifications(
        clientUID : String,
        clientTeamName: String,
        clientImageUrl: String,
        id : String,
        date : String,
        time: String,
        timeUtils : Long,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val hashMap : HashMap<String, String> = HashMap()
        hashMap["clientTeamName"] = clientTeamName
        hashMap["clientImageUrl"] = clientImageUrl
        hashMap["id"] = id
        hashMap["date"] = date
        hashMap["time"] = time
        hashMap["timeUtils"] = timeUtils.toString()

        firebaseDatabase.getReference("listNotifications").child(clientUID).push().setValue(hashMap)
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

    fun acceptRequestListNotification(
        clientUID : String,
        clientTeamName: String,
        clientImageUrl: String,
        id : String,
        date : String,
        time: String,
        timeUtils : Long,
        onSuccess : (String) -> Unit,
        onFail : (String) -> Unit
    ) {
        val hashMap : HashMap<String, String> = HashMap()
        hashMap["clientTeamName"] = clientTeamName
        hashMap["clientImageUrl"] = clientImageUrl
        hashMap["id"] = id
        hashMap["date"] = date
        hashMap["time"] = time
        hashMap["timeUtils"] = timeUtils.toString()

        firebaseDatabase.getReference("listNotifications").child(clientUID).push().setValue(hashMap)
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