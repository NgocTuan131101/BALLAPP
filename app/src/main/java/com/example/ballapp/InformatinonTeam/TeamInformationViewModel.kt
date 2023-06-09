package com.example.ballapp.InformatinonTeam

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballapp.account.Team.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class TeamInformationViewModel @Inject constructor(
    private val teamInformationRepository: TeamInformationRepository,
    private val teamRepository: TeamRepository
    ):ViewModel(){
        val loadTeamInfo = MutableLiveData<LoadTeamInfo>()
        val saveTeams = MutableLiveData<SaveTeams>()
        val saveTeamsImage = MutableLiveData<SaveTeamsImage>()

    sealed class LoadTeamInfo{
        object Loading :LoadTeamInfo()
        class LoadInfoOk(val teamName:String,val teamLocation :String,
                         val teamPeopleNumber: String,
                         val teamNote:String,val teamIamgeUrl :String):LoadTeamInfo()
    object LoadInfoError :LoadTeamInfo()
    }

    sealed class SaveTeams {
        object ResultOk : SaveTeams()
        object ResultError :SaveTeams()
    }
    sealed class SaveTeamsImage{
        object ResultOk : SaveTeamsImage()
        class ResultError(val errorMessage : String):SaveTeamsImage()
    }
    fun loadTeamInfo(
        userUID : String,
    ){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable -> throwable.printStackTrace()

        }){
            teamInformationRepository.loadTeamInfo(userUID,{
                if (it.exists()) {
                    val teamName = it.child("teamName").value.toString()
                    val teamLocation = it.child("teamLocation").value.toString()
                    val teamPeopleNumber = it.child("teamPeopleNumber").value.toString()
                    val teamNote = it.child("teamNote").value.toString()
                    val teamIamgeUrl = it.child("teamImageUrl").value.toString()
                    loadTeamInfo.value = LoadTeamInfo.LoadInfoOk(teamName,teamLocation,teamPeopleNumber,teamNote, teamIamgeUrl)
                }
            },{
                loadTeamInfo.value = LoadTeamInfo.LoadInfoError
            })
        }
    }
    fun saveTeams(
        teamUid :String,
        teamName : String,
        teamLocation: String,
        teamPeopleNumber: String,
        teamNote: String,
        deviceToken : String
    ){
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }){
            teamInformationRepository.updateTeamInfo(teamUid,teamName,teamLocation,teamPeopleNumber,teamNote,deviceToken,{
                saveTeams.value = SaveTeams.ResultOk
            },{
                saveTeams.value = SaveTeams.ResultError
            })
        }
    }
    fun saveTeamImage(
        imgUri: Uri,
        teamUid :String
    ){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable -> throwable.printStackTrace()
        }) {
            teamRepository.saveTeamImage(imgUri,teamUid,{
                saveTeamsImage.value = SaveTeamsImage.ResultOk
            },{
                saveTeamsImage.value = SaveTeamsImage.ResultError("")
            })
        }
    }
}