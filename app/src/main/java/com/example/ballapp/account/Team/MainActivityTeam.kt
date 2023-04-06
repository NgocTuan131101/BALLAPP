package com.example.ballapp.account.Team

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ballapp.R
import com.example.ballapp.utils.Animation
import com.example.ballapp.utils.MessageConnection
import com.example.ballapp.utils.Model.deviceToken
import com.example.ballapp.utils.Model.userAvatarUrl
import com.example.ballapp.utils.StorageConnection
import com.example.ballapp.databinding.ActivityMainTeamBinding
import com.example.ballapp.databinding.DailogNewNumberpeopleBinding
import com.example.ballapp.databinding.DialogNewLocationBinding
import com.example.ballapp.databinding.LoadingDialogBinding
import com.example.ballapp.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivityTeam : AppCompatActivity() {
    private lateinit var activityMainTeamBinding: ActivityMainTeamBinding
    private lateinit var imgUri: Uri
    private lateinit var newLocationBinding: DialogNewLocationBinding
    private lateinit var loadingDialogBinding: LoadingDialogBinding
    private lateinit var dailogNewNumberpeopleBinding: DailogNewNumberpeopleBinding
    private val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private val teamViewModel: TeamViewModel by viewModels()
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainTeamBinding = ActivityMainTeamBinding.inflate(layoutInflater)
        setContentView(activityMainTeamBinding.root)
        initEvents()
        initObserve()
    }

    private fun initObserve() {
        updateUserObserve()
        saveTeamImageObserve()
        saveTeamImageImageObserve()
    }

    private fun initEvents() {
        next()
        selectNumberPeople()// chọn người
        selectLocation() // chọn sân bóng
        selectTeamImage() // chọn ảnh team
        handleVariables()// xử lý cách biến
        back()
    }

    private fun saveTeamImageImageObserve() {
        teamViewModel.saveTeamsImage.observe(this) { result ->
            when (result) {
                is TeamViewModel.SaveTeamsImage.Loading -> {}
                is TeamViewModel.SaveTeamsImage.ResultOk -> {
                    Toast.makeText(this, "Thành công", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                    Animation.animateSlideLeft(this)
                }
                is TeamViewModel.SaveTeamsImage.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveTeamImageObserve() {
        teamViewModel.saveTeams.observe(this) { result ->
            when (result) {
                is TeamViewModel.SaveTeams.ResultOk -> {}
                is TeamViewModel.SaveTeams.ResultError -> {}
            }
        }
    }

    private fun updateUserObserve() {
        teamViewModel.updateUsers.observe(this) { result ->
            when (result) {
                is TeamViewModel.UpdateUsers.ResultOk -> {}
                is TeamViewModel.UpdateUsers.ResultError -> {}
            }
        }
    }

    private fun next() {
        dialog = Dialog(this, R.style.MyTimePickerTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        dialog.setContentView(loadingDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        activityMainTeamBinding.next.setOnClickListener {
            if (
                activityMainTeamBinding.editTeamName.text.isEmpty() ||
                activityMainTeamBinding.location.text.isEmpty() ||
                activityMainTeamBinding.peopleNumber.text.isEmpty()
            ) {
                Toast.makeText(this, "Vui lòng nhập dầy đủ thông tin ", Toast.LENGTH_SHORT).show()
            } else {
                dialog.show()
                if (this::imgUri.isInitialized) {
                    if (userUid != null) {
                        teamViewModel.saveTeamsImage(imgUri, userUid)
                        teamViewModel.saveTeams(
                            userUid,
                            activityMainTeamBinding.editTeamName.text.toString(),
                            activityMainTeamBinding.location.text.toString(),
                            activityMainTeamBinding.peopleNumber.text.toString(),
                            activityMainTeamBinding.note.text.toString(

                            ),
                            deviceToken!!
                        )
                        teamViewModel.updateUsers(
                            userUid,
                            activityMainTeamBinding.editTeamName.text.toString()
                        )
                    }
                }
                if (!this::imgUri.isInitialized) {
                    imgUri = Uri.parse("android.resource://$packageName/drawable/empty_team_image")
                    if (userUid != null) {
                        teamViewModel.saveTeamsImage(imgUri, userUid)
                        teamViewModel.saveTeams(
                            userUid, activityMainTeamBinding.editTeamName.text.toString(),
                            activityMainTeamBinding.location.text.toString(),
                            activityMainTeamBinding.peopleNumber.text.toString(),
                            activityMainTeamBinding.note.text.toString(),
                            deviceToken!!
                        )
                        teamViewModel.updateUsers(
                            userUid,
                            activityMainTeamBinding.editTeamName.text.toString()
                        )
                    }
                }
            }
        }
    }

    private fun selectNumberPeople() {
        activityMainTeamBinding.peopleNumber.setOnClickListener {
            showNumberPeopleSheetDialog()
        }
    }

    private fun showNumberPeopleSheetDialog() {
        val dialogNewberPeople = BottomSheetDialog(this,R.style.CustomBottomSheetDialog)
        dailogNewNumberpeopleBinding = DailogNewNumberpeopleBinding.inflate(layoutInflater)
        dialogNewberPeople.setContentView(dailogNewNumberpeopleBinding.root)

        dailogNewNumberpeopleBinding.five.setOnClickListener {
            activityMainTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.five.text
            dialogNewberPeople.dismiss()
            Animation.animateFade(this)
        }
        dailogNewNumberpeopleBinding.six.setOnClickListener {
            activityMainTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.six.text
            dialogNewberPeople.dismiss()
            Animation.animateFade(this)
        }
        dailogNewNumberpeopleBinding.seven.setOnClickListener {
            activityMainTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.seven.text
            dialogNewberPeople.dismiss()
            Animation.animateFade(this)
        }
        dailogNewNumberpeopleBinding.eight.setOnClickListener {
            activityMainTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.eight.text
            dialogNewberPeople.dismiss()
            Animation.animateFade(this)
        }
        dailogNewNumberpeopleBinding.nine.setOnClickListener {
            activityMainTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.nine.text
            dialogNewberPeople.dismiss()
            Animation.animateFade(this)
        }
        dailogNewNumberpeopleBinding.ten.setOnClickListener {
            activityMainTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.ten.text
            dialogNewberPeople.dismiss()
            Animation.animateFade(this)
        }
        dailogNewNumberpeopleBinding.eleven.setOnClickListener {
            activityMainTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.eleven.text
            dialogNewberPeople.dismiss()
            Animation.animateFade(this)
        }
        dialogNewberPeople.show()
    }

    private fun selectLocation() {
        activityMainTeamBinding.locationLayout.setOnClickListener {
            showLocationSheetDialog()
        }
    }

    private fun showLocationSheetDialog() {
        val dialogLocation = BottomSheetDialog(this, R.style.CustomBottomSheetDialog)
        newLocationBinding = DialogNewLocationBinding.inflate(layoutInflater)
        dialogLocation.setContentView(newLocationBinding.root)
        newLocationBinding.khoaHoc.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.khoaHoc.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }
        newLocationBinding.monaco.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.monaco.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }

        newLocationBinding.lamHoang.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.lamHoang.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }
        newLocationBinding.anCuu.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.anCuu.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }
        newLocationBinding.luat.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.luat.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }
        newLocationBinding.uyenPhuong.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.uyenPhuong.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }
        newLocationBinding.yDuoc.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.yDuoc.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }
        newLocationBinding.xuanPhu.setOnClickListener {
            activityMainTeamBinding.location.text = newLocationBinding.xuanPhu.text
            dialogLocation.dismiss()
            Animation.animateFade(this)
        }
        dialogLocation.show()
    }

    private fun handleVariables() {
        MessageConnection.firebaseMessaging.token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(
                        ContentValues.TAG,
                        "Fetching FCM registration toke failed",
                        task.exception
                    )
                    return@OnCompleteListener
                } else {
                    deviceToken = task.result
                }
            })
        StorageConnection.storageReference.getReference("Users").child(userUid!!).downloadUrl
            .addOnCompleteListener {
                userAvatarUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error",it.toString())
            }
    }

    private fun selectTeamImage() {
        activityMainTeamBinding.teamImage.setOnClickListener {
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
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, 0)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.data != null) {
            imgUri = data.data!!

            // Tạo drawable từ đối tượng Uri
            val drawable = Drawable.createFromStream(contentResolver.openInputStream(imgUri), imgUri.toString())

            // Thiết lập drawable cho ImageView trong CardView
            activityMainTeamBinding.teamImageImage.findViewById<ImageView>(R.id.team_image).setImageDrawable(drawable)
        }
    }

    private fun back() {
        activityMainTeamBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}