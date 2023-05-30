package com.example.ballapp.main.FragmentBattle.newcreata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewCreateFramentViewModel@Inject constructor(private val newCreateFragmentRepository: NewCreateFragmentRepository):
    ViewModel() {
    val loadNewCreate = MutableLiveData<LoadNewCreate>()
    val highLigt = MutableLiveData<HighLighResult>()

    sealed class HighLighResult {
        object HighLighResultOk : HighLighResult()
        object HighLighResultError : HighLighResult()
        object NotHighLighResultOK: HighLighResult()
        object NotHighLighResultError : HighLighResult()
    }

    sealed class LoadNewCreate {
        object  Loading : LoadNewCreate()
        class ResultOk(val list: ArrayList<CreateMatchModel>) : LoadNewCreate()
        object ResultError : LoadNewCreate()
    }
    fun loadNewCreate(userUID:String){
        viewModelScope.launch(CoroutineExceptionHandler{ _, throwable -> throwable.printStackTrace()
        }){
            newCreateFragmentRepository.loadNewCreate(userUID,{
                loadNewCreate.value = LoadNewCreate.ResultOk(it)
            },{
                loadNewCreate.value = LoadNewCreate.ResultError
            })
        }
    }
    fun handleHighLight(userUID: String,matchID: String){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable -> throwable.printStackTrace()
        }){
            newCreateFragmentRepository.highlight(userUID,matchID,{
                highLigt.value = HighLighResult.HighLighResultOk
            },{
                highLigt.value = HighLighResult.HighLighResultError
            })
        }
    }
    fun handleNotHingLight(userUID: String,matchID: String){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable -> throwable.printStackTrace()
        }){
            newCreateFragmentRepository.noHighLight(userUID,matchID,{
                highLigt.value = HighLighResult.NotHighLighResultOK
            },{
                highLigt.value = HighLighResult.NotHighLighResultError
            })

        }
    }
}
