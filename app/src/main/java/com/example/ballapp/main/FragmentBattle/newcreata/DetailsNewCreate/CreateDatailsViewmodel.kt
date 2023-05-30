package com.example.ballapp.main.FragmentBattle.newcreata.DetailsNewCreate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CreateDatailsViewmodel @Inject constructor(private val createDatailsRepository: CreateDatailsRepository) :ViewModel() {
    val deleteNewCreate = MutableLiveData<DeleteNewCreate>()
    sealed class DeleteNewCreate{
        object ResultOk : DeleteNewCreate()
        object ResultError : DeleteNewCreate()
    }
    fun deleteNewCreate(userUID : String , mathID: String){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable -> throwable.printStackTrace()
        }){
            createDatailsRepository.deteteCreateDatails(userUID,mathID,{
                deleteNewCreate.value = DeleteNewCreate.ResultOk
            },{
                deleteNewCreate.value = DeleteNewCreate.ResultError
            })
        }
    }

}