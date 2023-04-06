package com.example.ballapp.home.all.AllDetailsActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AllDetailsViewModel @Inject constructor(private val allDetailsRepository: AllDetailsRepository) :ViewModel() {
    val catchMatch = MutableLiveData<CatchMatch>()
    val waitMatchListNotification = MutableLiveData<WaitMatchListNotificationResult>()

    sealed class WaitMatchListNotificationResult {
        object ResultOk : WaitMatchListNotificationResult()
        object ResultError : WaitMatchListNotificationResult()
    }
    sealed class CatchMatch{
            object ResultOK : CatchMatch()
            object ResultError : CatchMatch()

            object WaitMatchOK :CatchMatch() // chờ trận đấu
            object WaitMatchError :CatchMatch()
        }

}