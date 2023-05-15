package com.example.ballapp.home.tomorrow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class TomorrowFragmentViewModel @Inject constructor(private val tomorrowRepository: TomorrowFragmentRepository) : ViewModel() {
    val loadTomorrowList = MutableLiveData<LoadTomorrowList>()

    sealed class LoadTomorrowList {
        object Loading: LoadTomorrowList()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadTomorrowList()
        class ResultError(val errorMessage : String) : LoadTomorrowList()
    }

    fun loadTomorrow(userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            tomorrowRepository.loadMatchList(userUID, {
                loadTomorrowList.value = LoadTomorrowList.ResultOk(it)
            }, {
                loadTomorrowList.value = LoadTomorrowList.ResultError(it)
            })
        }
    }
}