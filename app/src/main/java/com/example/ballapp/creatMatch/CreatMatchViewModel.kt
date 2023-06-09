package com.example.ballapp.creatMatch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.ballapp.InformatinonTeam.TeamInformationRepository
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CreatMatchViewModel @Inject constructor(
    private val teamInformationRepository: TeamInformationRepository,
    private val createMatchRepository: CreatMatchRepository,
) : ViewModel() {
    //  load thông tin nhóm
    val loadTeamInfo = MutableLiveData<LoadTeamInfo>()
    val saveRequest = MutableLiveData<SaveRequest>()
    val notification = MutableLiveData<NotificationResult>()
    val saveNewCreate = MutableLiveData<NewCreateResult>()


    sealed class LoadTeamInfo {
        object Loading : LoadTeamInfo()
        class LoadImageOk (val teamImageUrl: String) : LoadTeamInfo()
        object LoadImageError : LoadTeamInfo()
        class LoadInfoOk(val teamLocation: String, val teamPeopleNumber: String) : LoadTeamInfo()
        object LoadInfoError : LoadTeamInfo()
    }

    sealed class SaveRequest {
        object Loading : SaveRequest()
        object ResultOk : SaveRequest()
        object ResultError : SaveRequest()
    }

    sealed class NotificationResult {
        object ResultOk : NotificationResult()
        object ResultError : NotificationResult()
    }

    sealed class NewCreateResult {
        object ResultOk : NewCreateResult()
        object ResultError : NewCreateResult()
    }

    fun loadTeamInfo(
        userUID: String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            teamInformationRepository.loadTeamInfo(userUID, {
                if (it.exists()) {
                    val teamImageUrl = it.child("teamImageUrl").value.toString()
                    loadTeamInfo.value = LoadTeamInfo.LoadImageOk(teamImageUrl)
                }
            }, {
                loadTeamInfo.value = LoadTeamInfo.LoadInfoError
            })
            teamInformationRepository.loadTeamInfo(userUID, {
                if (it.exists()) {
                    val teamLocation = it.child("teamLocation").value.toString()
                    val teamPeopleNumber = it.child("teamPeopleNumber").value.toString()
                    loadTeamInfo.value = LoadTeamInfo.LoadInfoOk(teamLocation, teamPeopleNumber)
                }
            }, {
                loadTeamInfo.value = LoadTeamInfo.LoadInfoError
            })
        }
    }

    fun saveRequest(
        userUID : String,
        matchID : String,
        deviceToken : String,
        teamName : String,
        teamPhone : String,
        date : String,
        time : String,
        location : String,
        note : String,
        teamPeopleNumber: String,
        teamImageUrl: String,
        locationAddress: String,
        lat: Double,
        long: Double,
        geoHash: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            createMatchRepository.sendRequest(userUID,
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
                geoHash,
                {
                    saveRequest.value = SaveRequest.ResultOk
                },
                {
                    saveRequest.value = SaveRequest.ResultError
                })
        }
    }

    fun notification(
        matchID: String,
        teamName: String,
        userUID: String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            createMatchRepository.notification(matchID, teamName, userUID, {
                notification.value = NotificationResult.ResultOk
            }, {
                notification.value = NotificationResult.ResultError
            })
        }
    }

    fun saveNewCreate(
        userUID : String,
        matchID : String,
        deviceToken : String,
        teamName : String,
        teamPhone : String,
        date : String,
        time : String,
        location : String,
        note : String,
        teamPeopleNumber: String,
        teamImageUrl: String,
        locationAddress: String,
        lat: Double,
        long: Double,
        geoHash: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            createMatchRepository.saveNewCreate(userUID,
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
                geoHash,
                {
                    saveNewCreate.value = NewCreateResult.ResultOk
                },
                {
                    saveNewCreate.value = NewCreateResult.ResultError
                })
        }
    }
}