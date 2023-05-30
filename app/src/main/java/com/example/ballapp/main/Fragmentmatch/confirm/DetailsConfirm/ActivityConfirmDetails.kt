package com.example.ballapp.main.Fragmentmatch.confirm.DetailsConfirm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ballapp.R
import com.example.ballapp.databinding.ActivityConfirmDetailsBinding
import com.example.ballapp.databinding.DialogSuccessBinding
import com.example.ballapp.databinding.SignOutDialogBinding
import com.example.ballapp.databinding.SuccessDialogBinding
import com.example.ballapp.main.FragmentMessagen.MessagenDetails.ChatDetailsActivity
import com.example.ballapp.utils.Animation
import com.example.ballapp.utils.Model
import com.example.ballapp.utils.Model.click
import com.example.ballapp.utils.Model.clientImageUrl
import com.example.ballapp.utils.Model.clientPhone
import com.example.ballapp.utils.Model.clientTeamName
import com.example.ballapp.utils.Model.confirmUID
import com.example.ballapp.utils.Model.currentAddress
import com.example.ballapp.utils.Model.currentLat
import com.example.ballapp.utils.Model.currentLong
import com.example.ballapp.utils.Model.destinationAddress
import com.example.ballapp.utils.Model.destinationLat
import com.example.ballapp.utils.Model.destinationLong
import com.example.ballapp.utils.Model.deviceToken
import com.example.ballapp.utils.Model.geoHash
import com.example.ballapp.utils.Model.lat
import com.example.ballapp.utils.Model.locationAddress
import com.example.ballapp.utils.Model.long
import com.example.ballapp.utils.Model.matchDate
import com.example.ballapp.utils.Model.matchID
import com.example.ballapp.utils.Model.matchLocation
import com.example.ballapp.utils.Model.matchTime
import com.example.ballapp.utils.Model.teamImageUrl
import com.example.ballapp.utils.Model.teamName
import com.example.ballapp.utils.Model.teamNote
import com.example.ballapp.utils.Model.teamPeopleNumber
import com.example.ballapp.utils.Model.teamPhone
import com.example.ballapp.utils.Model.userImageUrl
import com.example.ballapp.utils.StorageConnection
import com.example.ballball.model.CreateMatchModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import io.grpc.Context
import java.util.*
import java.util.logging.Handler

@AndroidEntryPoint
class ActivityConfirmDetails : AppCompatActivity() {
    private lateinit var activityConfirmDetailBinding: ActivityConfirmDetailsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val confirmDetailsViewModel: ConfirmDetailsViewModel by viewModels()
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var successBinding: SuccessDialogBinding
    private val permissionId = 2

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: android.content.Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, ActivityConfirmDetails::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityConfirmDetailBinding = ActivityConfirmDetailsBinding.inflate(layoutInflater)
        setContentView(activityConfirmDetailBinding.root)
        initEvents()
        initObserves()


    }

    private fun initObserves() {
        denyConfirmMatchObserves()
        acceptMatchObserve()
        acceptMatch()
        saveUpComingClientObserve()
        denyRequestListNotification()
        acceptRequestListNotification()
    }

    private fun acceptRequestListNotification() {
        confirmDetailsViewModel.confirmRequestListNotification.observe(this){result ->
            when(result){
                is ConfirmDetailsViewModel.ConfirmRequestListNotification.ResultOk ->{}
                is ConfirmDetailsViewModel.ConfirmRequestListNotification.ResultError -> {}
            }
        }
    }

    private fun denyRequestListNotification() {
        confirmDetailsViewModel.denyRequestListNotification.observe(this){result ->
            when(result){
                is ConfirmDetailsViewModel.DenyRequestListNotification.ResultOk -> {}
                is ConfirmDetailsViewModel.DenyRequestListNotification.ResultError -> {}
            }
        }
    }

    private fun saveUpComingClientObserve() {
        confirmDetailsViewModel.saveUpComingClient.observe(this){result ->
            when(result){
                is ConfirmDetailsViewModel.UpComingClientResult.SaveUpComingClientOk -> {}
                is ConfirmDetailsViewModel.UpComingClientResult.SaveUpComingClientError -> {}
            }
        }
    }
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        currentLat = list[0].latitude
                        currentLong = list[0].longitude
                        currentAddress = list[0].getAddressLine(0)
                        Log.e("Address", currentAddress.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Hãy bật định vị của bạn", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled (
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun acceptMatchObserve() {
        confirmDetailsViewModel.acceptMatch.observe(this){result ->
            when(result){
                is ConfirmDetailsViewModel.AcceptMatch.SaveUpComingOk ->{
                    val dialog = Dialog(this,R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successBinding.text.text ="Thành Công"
                    successBinding.next.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                    val handler = android.os.Handler()
                    handler.postDelayed({
                        dialog.cancel()
                        finish()
                        Animation.animateSlideRight(this)
                    },5000)
                }
                is ConfirmDetailsViewModel.AcceptMatch.SaveUpComingError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteConfirmOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteConfirmError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteWaitOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteWaitError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.AcceptMatchNotificationOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.AcceptMatchNotificationError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DenyMatchNotificationOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DenyMatchNotificationError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteMatchOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteMatchError -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteNewCreateOk -> {}
                is ConfirmDetailsViewModel.AcceptMatch.DeleteNewCreateError -> {}
            }
        }
    }

    private fun denyConfirmMatchObserves() {
        confirmDetailsViewModel.denyConfirmMatch.observe(this) { result ->
            when (result) {
                is ConfirmDetailsViewModel.DenyConfirmMatch.ResultOk -> {
                    val dialog = Dialog(this,R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successBinding.root)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successBinding.text.text = "Bạn đã từ chối yêu cầu của $clientTeamName"
                    successBinding.next.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                    val handler = android.os.Handler()
                    handler.postDelayed({
                        dialog.cancel()
                        finish()
                        Animation.animateSlideRight(this)
                    },5000)
                }
                is  ConfirmDetailsViewModel.DenyConfirmMatch.ResultError -> {}
                else -> {}
            }
        }
    }

    private fun initEvents() {
        binding()
        handleVariables()
        back()
        denyConfirmMatch()
        acceptMatch()
        phoneCall()
        chat()
    }

    private fun chat() {
        activityConfirmDetailBinding.openChat.setOnClickListener {
            val intent = Intent(this, ChatDetailsActivity::class.java)
            intent.putExtra("teamName", clientTeamName)
            intent.putExtra("userUid", confirmUID)
            startActivity(intent)
            Animation.animateSlideLeft(this)
        }
    }

    private fun phoneCall() {
        activityConfirmDetailBinding.phoneCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.CALL_PHONE),
                    1
                )
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clientPhone"))
                startActivity(intent)
                Animation.animateSlideLeft(this)
            }
        }
    }

    private fun acceptMatch() {
        activityConfirmDetailBinding.acceptRequest.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = " Đồng ý"
            signOutDialogBinding.content.text =
                "Bạn đồng ý yêu cầu c ủa bắt trận của $clientTeamName?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    confirmDetailsViewModel.acceptMatch(
                        userUID,
                        matchID!!,
                        deviceToken!!,
                        teamName!!,
                        teamPhone!!,
                        matchDate!!,
                        matchTime!!,
                        matchLocation!!,
                        teamNote!!,
                        teamPeopleNumber!!,
                        teamImageUrl!!,
                        locationAddress!!,
                        lat!!,
                        long!!,
                        click!!,
                        clientTeamName!!,
                        clientImageUrl!!,
                        confirmUID!!,
                        confirmUID!!,
                        geoHash!!
                    )
                }
                if (userUID != null) {
                    confirmDetailsViewModel.saveUpComingClient(
                        userUID,
                        matchID!!,
                        deviceToken!!,
                        teamName!!,
                        teamPhone!!,
                        matchDate!!,
                        matchTime!!,
                        matchLocation!!,
                        teamNote!!,
                        teamPeopleNumber!!,
                        teamImageUrl!!,
                        locationAddress!!,
                        lat!!,
                        long!!,
                        click!!,
                        clientTeamName!!,
                        clientImageUrl!!,
                        confirmUID!!,
                        userUID,
                        geoHash!!
                    )
                }
                val timeUtils: Long = System.currentTimeMillis()
                confirmDetailsViewModel.confirmRequestListNotification(
                    confirmUID!!,
                    teamName!!,
                    userImageUrl!!,
                    "acceptRequest",
                    matchDate!!,
                    matchTime!!,
                    timeUtils
                )
                dialog.dismiss()
            }
            signOutDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun denyConfirmMatch() {
        activityConfirmDetailBinding.denyMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Từ chối"
            signOutDialogBinding.content.text =
                "Bạn muối từ chối yêu cầu bắt traanh của $clientTeamName?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    confirmDetailsViewModel.denyConfirmMatch(
                        userUID, matchID!!, confirmUID!!,
                        confirmUID!!, matchDate!!, matchTime!!, teamName!!
                    )
                    val timeUtils: Long = System.currentTimeMillis()
                    confirmDetailsViewModel.denyRequestListNotification(
                        confirmUID!!,
                        teamName!!,
                        userImageUrl!!,
                        "denyRequest",
                        matchDate!!,
                        matchTime!!,
                        timeUtils
                    )
                }
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }

    private fun back() {
        activityConfirmDetailBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun handleVariables() {
        FirebaseDatabase.getInstance().getReference("Users").child(confirmUID!!).get()
            .addOnSuccessListener {
                clientPhone = it.child("userPhone").value.toString()
            }
        StorageConnection.storageReference.getReference("Users").child(userUID!!).downloadUrl
            .addOnSuccessListener {
                userImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
            }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(activityConfirmDetailBinding) {
                teamName.text = data?.clientTeamName
                Glide.with(teamName).load(data?.clientTeamName).centerCrop().into(teamImage)
                date.text = data?.date
                time.text = data?.time
                peopleNumber.text = data?.teamPeopleNumber
                location.text = data?.location
                locationAddress.text = data?.locationAddress
                if (data?.note?.isEmpty() == true) {
                    note.text = "..."
                } else {
                    note.text = data?.note
                }
                matchID = data?.matchID
                deviceToken = data?.deviceToken
                Model.teamName = data?.teamName
                teamPhone = data?.teamPhone
                matchDate = data?.date
                matchTime = data?.time
                matchLocation = data?.location
                teamNote = data?.note
                teamPeopleNumber = data?.teamPeopleNumber
                teamImageUrl = data?.teamImageUrl
                Model.locationAddress = data?.locationAddress
                lat = data?.lat
                long = data?.long
                click = data?.click
                clientTeamName = data?.clientTeamName
                clientImageUrl = data?.clientImageUrl
                confirmUID = data?.confirmUID
                destinationLat = data?.lat
                destinationLong = data?.long
                destinationAddress = data?.locationAddress
                geoHash = data?.geoHash

            }
        }
    }
}