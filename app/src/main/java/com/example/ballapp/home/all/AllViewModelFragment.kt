package com.example.ballapp.home.all

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllViewModelFragment @Inject constructor(private val  allRepositoryFragment: AllRepositoryFragment):ViewModel() {
    val loadALLlist = MutableLiveData<LoadAllList>()
    val highligt = MutableLiveData<HighLighResult>()

    sealed class HighLighResult {
        object HighlightOk : HighLighResult()
        object HighlightErorr : HighLighResult()
        object NotHighlightOk : HighLighResult()
        object NotHighlightErorr : HighLighResult()

    }

    sealed class LoadAllList{
        object  Loading: LoadAllList()
        class ResultOK(val list: ArrayList<CreateMatchModel>) : LoadAllList()
        class ResultError(val errerMessage :String): LoadAllList()
    }
    fun loadAll(userUID : String){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable -> throwable.printStackTrace()
        }) {
            allRepositoryFragment.loadMatchList(userUID , {
                loadALLlist.value = LoadAllList.ResultOK(it)
            },{
                loadALLlist.value = LoadAllList.ResultError(it)
            })
        }
    }
    fun handlehighligt(matchID: String){
        viewModelScope.launch(CoroutineExceptionHandler{_ , throwable ->
            throwable.printStackTrace()
        }){
            allRepositoryFragment.highligt(matchID,{
                highligt.value = HighLighResult.HighlightOk

            },{
                highligt.value = HighLighResult.HighlightErorr
            })
        }

    }
    fun handlenothighligt(matchID: String){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable -> throwable.printStackTrace()
        }){
            allRepositoryFragment.notHighLight(matchID,{
                highligt.value = HighLighResult.NotHighlightOk
            },{
                highligt.value = HighLighResult.NotHighlightErorr
            })
        }
    }

}