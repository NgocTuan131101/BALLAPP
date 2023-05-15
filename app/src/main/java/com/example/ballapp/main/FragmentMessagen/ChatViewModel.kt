package com.example.ballapp.main.FragmentMessagen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballapp.model.UsersModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository) :ViewModel() {
    val loadChatList = MutableLiveData<LoadChatList>()

    sealed class LoadChatList{
        object Loading :LoadChatList()
        class ResultOk(val list : java.util.ArrayList<UsersModel>) :LoadChatList()
        object ResultError : LoadChatList()
    }
    fun loadChatList(userUID :String){
        viewModelScope.launch(CoroutineExceptionHandler{_,thowable -> thowable.printStackTrace()
        }){
            chatRepository.loadChatChannel(userUID,{
                loadChatList.value = LoadChatList.ResultOk(it)
            },{
                loadChatList.value = LoadChatList.ResultError
            })
        }
    }
}