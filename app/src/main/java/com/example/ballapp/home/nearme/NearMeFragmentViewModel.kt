package com.example.ballapp.home.nearme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearMeFragmentViewModel @Inject constructor(private val nearMeRepository: NearMeFragmentRepository) : ViewModel() {

    val loadNearMe = MutableLiveData<LoadNearMeResult>()

    sealed class LoadNearMeResult {
        object Loading : LoadNearMeResult()
        class ResultOk(val list: ArrayList<CreateMatchModel>) : LoadNearMeResult()
        object ResultError : LoadNearMeResult()
    }

    fun loadNearMeList(
        userUID : String,
        currentLat : Double,
        currentLong : Double
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            nearMeRepository.loadNearMe(userUID, currentLat, currentLong, {
                loadNearMe.value = LoadNearMeResult.ResultOk(it)
            }, {
                loadNearMe.value = LoadNearMeResult.ResultError
            })
        }
    }
}