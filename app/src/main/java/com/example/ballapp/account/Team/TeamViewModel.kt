package com.example.ballapp.account.Team

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(private val teamRepository: TeamRepository) : ViewModel() {
    val saveTeams = MutableLiveData<SaveTeams>()
    val saveTeamsImage = MutableLiveData<SaveTeamsImage>()
    val updateUsers = MutableLiveData<UpdateUsers>()

    sealed class UpdateUsers {
        object ResultOk : UpdateUsers()
        object ResultError : UpdateUsers()
    }

    sealed class SaveTeams {
        object ResultOk : SaveTeams()
        object ResultError : SaveTeams()
    }

    sealed class SaveTeamsImage {
        object Loading : SaveTeamsImage()
        object ResultOk : SaveTeamsImage()
        class ResultError(val errorMessage : String) : SaveTeamsImage()
    }

    fun saveTeams(
        teamUid: String,
        teamName: String,
        teamLocation: String,
        teamPeopleNumber: String,
        teamNote: String,
        deviceToken: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            teamRepository.saveTeam(teamUid, teamName, teamLocation, teamPeopleNumber, teamNote, deviceToken, {
                saveTeams.value = SaveTeams.ResultOk
            }, {
                saveTeams.value = SaveTeams.ResultError
            })
        }
    }

    fun saveTeamsImage(
        imgUri : Uri,
        teamUid : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            teamRepository.saveTeamImage(imgUri, teamUid, {
                saveTeamsImage.value = SaveTeamsImage.ResultOk
            }, {
                saveTeamsImage.value = SaveTeamsImage.ResultError("")
            })
        }
    }

    fun updateUsers(
        userUID : String,
        teamName : String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            teamRepository.updateUser(userUID, teamName, {
                updateUsers.value = UpdateUsers.ResultOk
            }, {
                updateUsers.value = UpdateUsers.ResultError
            })
        }
    }
}