package com.example.ballapp.home.all.AllDetailsActivity

import androidx.core.util.TimeUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllDetailsViewModel @Inject constructor(private val allDetailsRepository: AllDetailsRepository) :
    ViewModel() {
    val catchMatch = MutableLiveData<CatchMatch>()
    val waitMatchListNotification = MutableLiveData<WaitMatchListNotificationResult>()

    sealed class WaitMatchListNotificationResult {
        object ResultOk : WaitMatchListNotificationResult()
        object ResultError : WaitMatchListNotificationResult()
    }

    sealed class CatchMatch {
        object ResultOK : CatchMatch()
        object ResultError : CatchMatch()

        object WaitMatchOK : CatchMatch() // chờ trận đấu
        object WaitMatchError : CatchMatch()
        object WaitMatchNotificationOk : CatchMatch()
        object WaitMatchErrorNotificationError : CatchMatch()
        object ConfirmMatchOk : CatchMatch()
        object ConfirmMatchError : CatchMatch()
    }
    fun handleCatchMatch(
        uID: String, userUID : String, waitUID: String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientUID : String, clientImageUrl: String, teamWaitUID : String, confirmUID: String,
        geoHash : String, clientClickNumber : Int
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.catchMatch(matchID, userUID, clientUID, click, {
                catchMatch.value = CatchMatch.ResultOK
            }, {
                catchMatch.value = CatchMatch.ResultError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.waitMatch(uID, userUID, matchID, deviceToken, teamName, teamPhone, date, time, location,
                note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click, clientTeamName, clientImageUrl,
                confirmUID, geoHash, clientClickNumber, {
                    catchMatch.value = CatchMatch.WaitMatchOK
                }, {
                    catchMatch.value = CatchMatch.WaitMatchError
                })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.waitMatchNotification(userUID, matchID, date, time, clientTeamName, {
                catchMatch.value = CatchMatch.WaitMatchNotificationOk
            }, {
                catchMatch.value = CatchMatch.WaitMatchErrorNotificationError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.confirmMatch(waitUID, matchID, deviceToken, teamName, teamPhone, date,
                time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
                clientTeamName, clientImageUrl, teamWaitUID, clientUID, geoHash, {
                    catchMatch.value = CatchMatch.ConfirmMatchOk
                }, {
                    catchMatch.value = CatchMatch.ConfirmMatchError
                })
        }
    }


    fun waiMatchListNotification(
            clinentUID: String,
            clientTeamName: String,
            clientImageUrl: String,
            id: String,
            date: String,
            time: String,
            timeUtils: Long
        ) {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
            }) {
                allDetailsRepository.waitMatchListNotification(
                    clinentUID,
                    clientTeamName,
                    clientImageUrl,
                    id,
                    date,
                    time,
                    timeUtils,
                    {
                        waitMatchListNotification.value = WaitMatchListNotificationResult.ResultOk
                    },
                    {
                        waitMatchListNotification.value =
                            WaitMatchListNotificationResult.ResultError
                    })
            }
        }
    }
