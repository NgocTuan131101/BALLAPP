package com.example.ballapp.InformatinonTeam

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat

import com.example.ballapp.databinding.*
import com.example.ballapp.utils.Animation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class MainActivityInfomationTeam : AppCompatActivity() {
    private lateinit var activityMainInfomationTeamBinding:ActivityMainInfomationTeamBinding
    private  var userUID = FirebaseAuth.getInstance().currentUser?.uid
    private  val teamInformationViewModel: TeamInformationViewModel by viewModels()
    private lateinit var imgUri : Uri
    private lateinit var loadingDialog: Dialog
    private lateinit var newLocationBinding: DialogNewLocationBinding
    private lateinit var dailogNewNumberpeopleBinding: DailogNewNumberpeopleBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private lateinit var loadingDialogBinding: LoadingDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainInfomationTeamBinding = ActivityMainInfomationTeamBinding.inflate(layoutInflater)
        setContentView(activityMainInfomationTeamBinding.root)
        initEvents()
        initObserve()

    }

    private fun initEvents() {

    }

    private fun initObserve() {
        back()
        locationSelect()
        peopleNumberSelect()
        selectTeamImage()

    }

    private fun selectTeamImage() {
        activityMainInfomationTeamBinding.teamImageLayout.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),100)
            }else{
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent,0)
                Animation.animateSlideLeft(this)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == RESULT_OK){
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
                activityMainInfomationTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.five.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.six.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.six.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.seven.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.seven.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.eight.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.eight.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.nine.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.nine.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.ten.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.ten.text
                DialogPeopleNumber.dismiss()
                Animation.animateFade(this)
            }
            dailogNewNumberpeopleBinding.eleven.setOnClickListener {
                activityMainInfomationTeamBinding.peopleNumber.text = dailogNewNumberpeopleBinding.eleven.text
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
                activityMainInfomationTeamBinding.location.text= newLocationBinding.uyenPhuong.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.yDuoc.setOnClickListener {
                activityMainInfomationTeamBinding.location.text = newLocationBinding.yDuoc.text
                DialogLocation.dismiss()
                Animation.animateFade(this)
            }
            newLocationBinding.xuanPhu.setOnClickListener {
                activityMainInfomationTeamBinding.location.text= newLocationBinding.xuanPhu.text
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