package com.example.ballapp.main.FragmentHome

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class FragmentHomeViewModel @Inject constructor(
    private val fragmentHomeRepository: FragmentHomeRepository
    ) : ViewModel() {
    val loadAvatar = MutableLiveData<LoadAvatar>()

    sealed class LoadAvatar {
        class ResultOK(val image: Bitmap) : LoadAvatar()
        object ResultError : LoadAvatar()
    }

    fun loadAvatar(
        userUID: String,
        loadFile: File
    ) { viewModelScope.launch(CoroutineExceptionHandler { _, throwable -> throwable
        .printStackTrace()
    }){
        fragmentHomeRepository.loadAvatar(userUID,loadFile,{
            loadAvatar.value = LoadAvatar.ResultError

        },{
            loadAvatar.value= LoadAvatar.ResultOK(it)
        })
    }
    }
}