package com.example.ballapp.UserAndAnnouncementAndSearch.User

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.ballapp.InformatinonTeam.MainActivityInfomationTeam
import com.example.ballapp.R
import com.example.ballapp.account.Team.MainActivityTeam
import com.example.ballapp.databinding.ActivityMainUserBinding
import com.example.ballapp.databinding.LayoutBottomSheetDialogBinding
import com.example.ballapp.databinding.LoadingDialogBinding
import com.example.ballapp.databinding.SignOutDialogBinding
import com.example.ballapp.databinding.SuccessDialogBinding
import com.example.ballapp.onboardingPhone.Login.MainActivityLogin
import com.example.ballapp.utils.Animation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.selects.select
import java.io.ByteArrayOutputStream
@AndroidEntryPoint
class MainActivityUser : AppCompatActivity() {
    private lateinit var mainActivityUserBinding:ActivityMainUserBinding
    private val userViewModel : UserViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var imgUri: Uri
    private lateinit var loadingDialog: Dialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding
    private lateinit var layoutBottomSheetDialogBinding: LayoutBottomSheetDialogBinding
    private lateinit var dialogSuccessBinding: SuccessDialogBinding
    private lateinit var signOutDialogBinding: SignOutDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityUserBinding = ActivityMainUserBinding.inflate(layoutInflater)
        setContentView(mainActivityUserBinding.root)
        initEvents()
        initObserve()
        if(userUID != null){
            userViewModel.loadUserInfo(userUID)
        }
    }

    private fun initObserve() {
        loadUserInfoObserve()
        saveAvatarObserve()
    }

    private fun loadUserInfoObserve() {
        userViewModel.loadUserInfo.observe(this){result ->
            with(mainActivityUserBinding){
                progressBar.visibility = View.GONE
                titleLayout.visibility = View.VISIBLE
                userAvatarImage.visibility = View.VISIBLE
                userName.visibility = View.VISIBLE
                userPhoneNumber.visibility = View.VISIBLE
                viewLine.visibility = View.VISIBLE
                contentLayout.visibility = View.VISIBLE
                sginOut.visibility =View.VISIBLE
            }
            when(result){
                is UserViewModel.LoadUserData.Loading ->{
                    mainActivityUserBinding.progressBar.visibility = View.VISIBLE
                }
                is UserViewModel.LoadUserData.LoadPhoneAndNameOk->{
                    mainActivityUserBinding.userName.text = result.userName
                    mainActivityUserBinding.userPhoneNumber.text = result.userPhone
                    Glide.with(mainActivityUserBinding.userAvatarImage).load(result.avatarUrl).centerCrop().into(mainActivityUserBinding.userAvatarImage)
                }
                is UserViewModel.LoadUserData.LoadPhoneAndNameError -> {}
                else -> {}
            }
        }
    }

    private fun initEvents() {
        back()
        editAvart()
        teamInformation()
        signOut()
    }

    private fun signOut() {
        mainActivityUserBinding.sginOut.setOnClickListener {
            showSignOutDialog()
        }
    }

    private fun showSignOutDialog() {
        val dialog = Dialog(this,R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
        dialog.setContentView(signOutDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        signOutDialogBinding.yes.setOnClickListener {
            dialog.dismiss()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,MainActivityLogin::class.java))
            Toast.makeText(this,"Đăng xuất thành công",Toast.LENGTH_SHORT).show()
            finishAffinity()
            Animation.animateSlideLeft(this)
        }
        signOutDialogBinding.no.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun saveAvatarObserve() {
        userViewModel.saveAvatar.observe(this){resutlt ->
            when(resutlt){
                is UserViewModel.SaveAvatar.ResultOk ->{
                    val  dialog = Dialog(this,R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialogSuccessBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(dialogSuccessBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialogSuccessBinding.text.text = "Thay đổi ảnh đại diện thành công"
                    dialogSuccessBinding.next.setOnClickListener {
                        dialog.cancel()
                    }
                    loadingDialog.dismiss()
                    dialog.show()
                    val handler =Handler()
                    handler.postDelayed({
                        dialog.cancel()
                    },5000)
                }
                is UserViewModel.SaveAvatar.ResultError->{
                    Toast.makeText(this,resutlt.errorMessage,Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun teamInformation() {
        mainActivityUserBinding.teamInformation.setOnClickListener {
            startActivity(Intent(this,MainActivityInfomationTeam::class.java))
            Animation.animateSlideLeft(this)
        }
    }

    private fun editAvart() {
        mainActivityUserBinding.userAvatarImage.setOnClickListener{
            sheetBottomSheetDialog()
        }
    }

    private fun sheetBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        layoutBottomSheetDialogBinding = LayoutBottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(layoutBottomSheetDialogBinding.root)

        layoutBottomSheetDialogBinding.takePhoto.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
            }else{
                selectAvatarFromCamera()
                dialog.dismiss()
                Animation.animateSlideLeft(this)
            }
        }
        layoutBottomSheetDialogBinding.gallery.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),100)

            }else{
                selectAvatar()
                dialog.dismiss()
                Animation.animateSlideLeft(this)
            }
        }
        dialog.show()
    }


    private fun selectAvatar() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,0)
    }
    @SuppressLint("QueryPermissionsNeeded")
    private fun selectAvatarFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent,1)
        }else{
            Toast.makeText(this,"Camera không hoạt động",Toast.LENGTH_SHORT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadingDialog = Dialog(this,R.style.MyAlertDialogTheme)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingDialog.setContentView(loadingDialogBinding.root)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (requestCode == 0 && resultCode == RESULT_OK){
            loadingDialog.show()
            imgUri = data?.data!!
            mainActivityUserBinding.userAvatarImage.setImageURI(imgUri)
        }
        if(requestCode == 1 && resultCode == RESULT_OK){
            loadingDialog.show()
            val bundle: Bundle? = data?.extras
            val photo : Bitmap = bundle?.get("data") as Bitmap
            val byte = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG,100,byte)
            val path = MediaStore.Images.Media.insertImage(this.contentResolver,photo,"Title",null)
            imgUri = Uri.parse(path)
            mainActivityUserBinding.userAvatarImage.setImageURI(imgUri)

        }
        if(this::imgUri.isInitialized){
            if(userUID != null)
                userViewModel.saveAvatar(imgUri,userUID)
        }
    }


    private fun back() {
        mainActivityUserBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }
}