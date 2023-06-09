package com.example.ballapp.model

import com.google.gson.annotations.SerializedName

data class AcceptMatchNotification(
    @SerializedName("clientUID")
    val clientUID : String,
    @SerializedName("userUID")
    val userUID: String = "",
    @SerializedName("matchID")
    val matchID: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("teamName")
    val teamName: String = ""
)