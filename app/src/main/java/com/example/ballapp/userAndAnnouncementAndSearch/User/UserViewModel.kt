package com.example.ballapp.UserAndAnnouncementAndSearch.User

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.user.walkthrough.avatar.AvatarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
/*
         trong constructor của "UserViewModel" nó được inject của dependencies như
          "userRepository" , "AvatarRepository"
          "LoadUserData" và "SaveAvatar" đều là sealed class định nghĩa cách trạng thái có thể xảy ra
          loadUserInfo : được sử dụng để lấy thông tin người dùng dựa trên userUID
          được truyền vào sử dụng "loadUserInfo" để gọi phương thức
         -  "loadPhoneAndName" để lấy thông tin từ firebase được trả kết quả sẽ được dựa vào biến
            "loadUserInfor"
         - saveAvatar : được sử dụng đẻ lưu ảnh đai điện người dùng "imgUri" và "UserUID"
           được sử dụng "avatarRepository" được gọi phương thức "saveAvatar"
           lưu ảnh vào Firebase, kết quả sẽ được dựa vào biến " saveAvatar"
 */
@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository, private val avatarRepository: AvatarRepository) :ViewModel() {
    val loadUserInfo = MutableLiveData<LoadUserData>()
    val saveAvatar = MutableLiveData<SaveAvatar>()

    sealed class SaveAvatar{
        object ResultOk :SaveAvatar()
        class ResultError(val errorMessage:String):SaveAvatar()

    }
    sealed class LoadUserData {
        object Loading : LoadUserData()
        class LoadPhoneAndNameOk(val userName:String , val userPhone: String,val avatarUrl:String) :LoadUserData()
        object LoadPhoneAndNameError:LoadUserData()
    }
    fun loadUserInfo(
        userUID :String
    ){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable ->
            throwable.printStackTrace()
        }){
            userRepository.loadPhoneAndName(userUID,{
                if(it.exists()){
                    val userName = it.child("userName").value.toString()
                    val userPhone = it.child("userPhone").value.toString()
                    val avatarUrl = it.child("avatarUrl").value.toString()
                    loadUserInfo.value = LoadUserData.LoadPhoneAndNameOk(userName,userPhone,avatarUrl)
                }
            },{
                loadUserInfo.value = LoadUserData.LoadPhoneAndNameError
            })
        }
    }
    fun saveAvatar(imgUri:Uri , userUID: String){
        viewModelScope.launch(CoroutineExceptionHandler{_,throwable ->
            throwable.printStackTrace()
        }){
            avatarRepository.saveAvatar(imgUri,userUID,{
                saveAvatar.value = SaveAvatar.ResultOk
            },{
                saveAvatar.value = SaveAvatar.ResultError(it)
            })
        }
    }


}
