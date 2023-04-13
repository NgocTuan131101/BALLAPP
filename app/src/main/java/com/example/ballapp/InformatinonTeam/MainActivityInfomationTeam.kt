package com.example.ballapp.InformatinonTeam

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.ballapp.R

import com.example.ballapp.databinding.*
import com.example.ballapp.utils.Animation
import com.example.ballapp.utils.MessageConnection
import com.example.ballapp.utils.Model.deviceToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityInfomationTeam : AppCompatActivity() {
    private lateinit var activityMainInfomationTeamBinding: ActivityMainInfomationTeamBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val teamInformationViewModel: TeamInformationViewModel by viewModels()
    private lateinit var imgUri: Uri
    private lateinit var loadingDialog: Dialog
    private lateinit var newLocationBinding: DialogNewLocationBinding
    private lateinit var dailogNewNumberpeopleBinding: DailogNewNumberpeopleBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private lateinit var loadingDialogBinding: LoadingDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainInfomationTeamBinding =
            ActivityMainInfomationTeamBinding.inflate(layoutInflater)
        setContentView(activityMainInfomationTeamBinding.root)
        initEvents()
        initObserve()
        if(userUID != null){
            teamInformationViewModel.loadTeamInfo(userUID)
        }

    }

    private fun initEvents() {
        teamInfoOb()
        saveTeamImagOb()
        editTeamOb()
    }
    private fun initObserve() {
        handleVariables()
        back()
        locationSelect()
        peopleNumberSelect()
        selectTeamImage()
        save()

    }


    private fun editTeamOb() {
        teamInformationViewModel.saveTeams.observe(this) { resurl ->
            when (resurl) {
                is TeamInformationViewModel.SaveTeams.ResultOk -> {
                    Toast.makeText(this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show()
                }
                is TeamInformationViewModel.SaveTeams.ResultError -> {}
            }
        }
    }



    private fun saveTeamImagOb() {
        teamInformationViewModel.saveTeamsImage.observe(this) { resutl ->
            when (resutl) {
                is TeamInformationViewModel.SaveTeamsImage.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyTimePickerTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    activityMainInfomationTeamBinding =
                        ActivityMainInfomationTeamBinding.inflate(layoutInflater)
                    dialog.setContentView(activityMainInfomationTeamBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Lưu thông tin thành công"
                    successDialogBinding.next.setOnClickListener {
                        dialog.cancel()
                    }
                    loadingDialog.dismiss()
                    dialog.show()
                    val handle = Handler()
                    handle.postDelayed({
                        dialog.cancel()
                    }, 50000)
                }
                is TeamInformationViewModel.SaveTeamsImage.ResultError -> {
                    Toast.makeText(this, resutl.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun teamInfoOb() {
        teamInformationViewModel.loadTeamInfo.observe(this) { result ->
            with(activityMainInfomationTeamBinding) {
                progressBar.visibility = View.GONE
                titleLayout.visibility = View.VISIBLE
                scrollLayout.visibility = View.VISIBLE
            }
            when (result) {
                is TeamInformationViewModel.LoadTeamInfo.Loading -> {
                    activityMainInfomationTeamBinding.progressBar.visibility = View.VISIBLE
                }
                is TeamInformationViewModel.LoadTeamInfo.LoadInfoOk -> {
                    activityMainInfomationTeamBinding.editTeamName.setText(result.teamName)
                    activityMainInfomationTeamBinding.location.text = result.teamLocation
                    activityMainInfomationTeamBinding.peopleNumber.text = result.teamPeopleNumber
                    activityMainInfomationTeamBinding.note.setText(result.teamNote)
                    Glide.with(activityMainInfomationTeamBinding.teamImage)
                        .load(result.teamIamgeUrl)
                        .centerCrop()
                        .into(activityMainInfomationTeamBinding.teamImage)
                }
                is TeamInformationViewModel.LoadTeamInfo.LoadInfoError -> {}
            }
        }
    }


    private fun save() {
        loadingDialog = Dialog(this, R.style.MyTimePickerTheme)
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingDialog.setContentView(loadingDialogBinding.root)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        activityMainInfomationTeamBinding.save.setOnClickListener {
            if (activityMainInfomationTeamBinding.editTeamName.text.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đội", Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.show()
                if (this::imgUri.isInitialized) {
                    if (userUID != null) {
                        teamInformationViewModel.saveTeamImage(imgUri, userUID)
                    }
                }
                if (userUID != null) {
                    teamInformationViewModel.saveTeams(
                        userUID,
                        activityMainInfomationTeamBinding.editTeamName.text.toString(),
                        activityMainInfomationTeamBinding.location.text.toString(),
                        activityMainInfomationTeamBinding.peopleNumber.text.toString(),
                        activityMainInfomationTeamBinding.note.text.toString(),
                        deviceToken!!
                    )
                }
            }
        }
    }

    private fun handleVariables() {
        MessageConnection.firebaseMessaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(
                        ContentValues.TAG,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                } else {
                    deviceToken = task.result
                }
            })
    }

    private fun selectTeamImage() {
        activityMainInfomationTeamBinding.teamImageLayout.setOnClickListener {
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
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, 0)
                Animation.animateSlideLeft(this)

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            imgUri = data?.data!!
            activityMainInfomationTeamBinding.teamImage.setImageURI(imgUri)
        }
    }

    private fun peopleNumberSelect() {
        activityMainInfomationTeamBinding.peopleNumber.setOnClickListener {
            showPeopleNumberSelect()
        }
    }

    private fun showPeopleNumberSelect() {
        activityMainInfomationTeamBinding.peopleNumber.setOnClickListener {
            val DialogPeopleNumber = BottomSheetDialog(this)
            dailogNewNumberpeopleBinding = DailogNewNumberpeopleBinding.inflate(layoutInflater)
            DialogPeopleNumber.setContentView(dailogNewNumberpeopleBinding.root)

            dailogNewNumberpeopleBinding.five.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text =
                    dailogNewNumberpeopleBinding.five.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.six.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text =
                    dailogNewNumberpeopleBinding.six.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.seven.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text =
                    dailogNewNumberpeopleBinding.seven.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.eight.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text =
                    dailogNewNumberpeopleBinding.eight.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.nine.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text =
                    dailogNewNumberpeopleBinding.nine.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.ten.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text =
                    dailogNewNumberpeopleBinding.ten.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.eleven.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text =
                    dailogNewNumberpeopleBinding.eleven.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            DialogPeopleNumber.show()
        }
    }

    private fun locationSelect() {
        activityMainInfomationTeamBinding.location.setOnClickListener {
            showLocationBottomSheetDialog()
        }
    }

    private fun showLocationBottomSheetDialog() {
        activityMainInfomationTeamBinding.location.setOnClickListener {
            val DialogLocation = BottomSheetDialog(this)
            newLocationBinding = DialogNewLocationBinding.inflate(layoutInflater)
            DialogLocation.setContentView(newLocationBinding.root)
            newLocationBinding.khoaHoc.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.khoaHoc.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.monaco.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.monaco.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.lamHoang.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.lamHoang.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.anCuu.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.anCuu.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.luat.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.luat.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.uyenPhuong.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.uyenPhuong.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.yDuoc.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.yDuoc.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.xuanPhu.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.xuanPhu.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            DialogLocation.show()
        }
    }

    private fun back() {
        activityMainInfomationTeamBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }


}