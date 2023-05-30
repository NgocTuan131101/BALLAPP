package com.example.ballapp.main.Fragmentmatch.wait

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ballapp.main.Fragmentmatch.upcoming.UpComingViewModel
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WaitViewModel @Inject constructor(private val waitRepository: WaitRepository):ViewModel()  {
    val loadWait = MutableLiveData<LoadWaitResutl>()
    val highList = MutableLiveData<HighLightResult>()
    sealed class LoadWaitResutl {
        object Loading :LoadWaitResutl()
        class ResultOk(val list : ArrayList<CreateMatchModel>):LoadWaitResutl()
        object ResultError : LoadWaitResutl()
    }
    sealed class HighLightResult{
        object HighLightOk :HighLightResult()
        object HighLightError :HighLightResult()
        object NoHighLightOk :HighLightResult()
        object NoHighLightEroor :HighLightResult()
    }
}