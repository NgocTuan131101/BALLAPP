package com.example.ballapp.account.Avatar

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.ballapp.account.Team.MainActivityTeam
import com.example.ballapp.R
import com.example.ballapp.utils.Animation
import com.example.ballapp.databinding.ActivityMainAvatarBinding
import com.example.ballapp.databinding.LayoutBottomSheetDialogBinding
import com.example.ballball.user.walkthrough.avatar.AvatarViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class MainActivityAvatar : AppCompatActivity() {
    private lateinit var mainActivityAvatar: ActivityMainAvatarBinding
    private lateinit var layoutBottomSheetDialogBinding: LayoutBottomSheetDialogBinding
    private lateinit var imgUri: Uri
    private val avatarViewModel: AvatarViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityAvatar = ActivityMainAvatarBinding.inflate(layoutInflater)
        setContentView(mainActivityAvatar.root)
        initEvents()
        initAvatarOb()
    }
    private fun initEvents() {
        back()
        next()
        selectAvatar()
    }
    private fun initAvatarOb() {
        avatarViewModel.saveAvatar.observe(this) { result ->
            when (result) {
                is AvatarViewModel.SaveAvatar.ResultOk -> {}
                is AvatarViewModel.SaveAvatar.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun selectAvatar() {
        mainActivityAvatar.progressPicture.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        layoutBottomSheetDialogBinding = LayoutBottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(layoutBottomSheetDialogBinding.root)

        layoutBottomSheetDialogBinding.takePhoto.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    100
                )
            } else {
                selectAvatarFromCamera()
                dialog.dismiss()
                Animation.animateSlideLeft(this)
            }
        }
        layoutBottomSheetDialogBinding.gallery.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    100
                )

            } else {
                selectAvatarFromgallery()
                dialog.dismiss()
                Animation.animateSlideLeft(this)
            }
        }
        dialog.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            imgUri = data?.data!!
            mainActivityAvatar.progressPicture.setImageURI(imgUri)
        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val bundle: Bundle? = data?.extras
            val finalPhoto: Bitmap = bundle?.get("data") as Bitmap
            val bytes = ByteArrayOutputStream()
            finalPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(this.contentResolver, finalPhoto, "Title", null)
            imgUri = Uri.parse(path)
            mainActivityAvatar.progressPicture.setImageURI(imgUri)
        }
    }

    private fun selectAvatarFromgallery() {
        val intent = Intent()
        intent.type = "*image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 0)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun selectAvatarFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 1)
        } else {
            Toast.makeText(this, "Camera không hoạt động !", Toast.LENGTH_SHORT).show()
        }
    }


    private fun next() {
        mainActivityAvatar.next.setOnClickListener {
            if (this::imgUri.isInitialized) {
                if (userUID != null) {
                    avatarViewModel.saveAvatar(imgUri, userUID)
                }
                startActivity(Intent(this, MainActivityTeam::class.java))
                Animation.animateSlideLeft(this)
            } else {
                imgUri = Uri.parse("android.resource://$packageName/drawable/empty_avatar")
                if (userUID != null) {
                    avatarViewModel.saveAvatar(imgUri, userUID)
                }
                startActivity(Intent(this, MainActivityTeam::class.java))
                Animation.animateSlideLeft(this)
            }
        }
    }

    private fun back() {
        mainActivityAvatar.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}