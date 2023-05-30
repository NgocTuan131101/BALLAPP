package com.example.ballapp.main.Fragmentmatch.upcoming.DetailsUpcoming

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ballapp.R
import com.example.ballapp.databinding.ActivityUpcomingdetailsBinding
import com.example.ballapp.databinding.CancelMatchDialogBinding
import com.example.ballapp.databinding.DialogSuccessBinding
import com.example.ballapp.databinding.SuccessDialogBinding
import com.example.ballapp.main.FragmentMessagen.MessagenDetails.ChatDetailsActivity
import com.example.ballapp.utils.Animation
import com.example.ballapp.utils.Model
import com.example.ballapp.utils.Model.clientPhone
import com.example.ballapp.utils.Model.clientTeamName
import com.example.ballapp.utils.Model.clientUID
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
import com.example.ballapp.utils.Model.uid
import com.example.ballapp.utils.Model.userImageUrl
import com.example.ballapp.utils.StorageConnection
import com.example.ballball.model.CreateMatchModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ActivityUpcomingDetails : AppCompatActivity() {
    private lateinit var activityUpcomingdetailsBinding: ActivityUpcomingdetailsBinding
    private val upComingDetailsViewModel: UpComingDetailsViewModel by viewModels()
    private lateinit var cancelMatchDialogBinding: CancelMatchDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var successBinding: SuccessDialogBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionId = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUpcomingdetailsBinding = ActivityUpcomingdetailsBinding.inflate(layoutInflater)
        setContentView(activityUpcomingdetailsBinding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
        initObserve()
    }

    private fun initObserve() {
        cancelUpComingMatchObserve()
        cancelUpComingListNotification()
        restoreMatchObserve()
    }

    private fun restoreMatchObserve() {
        upComingDetailsViewModel.restoreMatch.observe(this) {result ->
            when (result) {
                is UpComingDetailsViewModel.RestoreMatch.ResultOk -> {}
                is UpComingDetailsViewModel.RestoreMatch.ResultError -> {}
            }
        }
    }
    private fun cancelUpComingListNotification() {
        upComingDetailsViewModel.cancelUpComingListNotification.observe(this) {result ->
            when (result) {
                is UpComingDetailsViewModel.CancelUpComingListNotification.ResultOk -> {}
                is UpComingDetailsViewModel.CancelUpComingListNotification.ResultError -> {}
            }
        }
    }
    private fun cancelUpComingMatchObserve() {
        upComingDetailsViewModel.cancelUpComing.observe(this) {result ->
            when (result) {
                is UpComingDetailsViewModel.CancelUpComing.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyAlertDialogTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successBinding.text.text = "Trận đấu này đã được hủy"
                    successBinding.next.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                    val handler = Handler()
                    handler.postDelayed({
                        dialog.cancel()
                        finish()
                        Animation.animateSlideRight(this)
                    }, 5000)
                }
                is UpComingDetailsViewModel.CancelUpComing.ResultError -> {}
                is UpComingDetailsViewModel.CancelUpComing.CancelNotificationOk -> {}
                is UpComingDetailsViewModel.CancelUpComing.CancelNotificationError -> {}
            }
        }
    }


    private fun initEvents() {
        binding()
        handleVariable()
        phoneCall()
        back()
        chat()
        cancelMatch()
    }

    private fun cancelMatch() {
        activityUpcomingdetailsBinding.catchMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyAlertDialogTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            cancelMatchDialogBinding = CancelMatchDialogBinding.inflate(layoutInflater)
            dialog.setContentView(cancelMatchDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            cancelMatchDialogBinding.yes.setOnClickListener {
                val id: Int = cancelMatchDialogBinding.radioGroup.checkedRadioButtonId
                if (id != -1) {
                    val radio : RadioButton = cancelMatchDialogBinding.root.findViewById(id)
                    val radioText = radio.text.toString()
                    if(userUID != null){
                        upComingDetailsViewModel.cancelUpComingMatch(clientUID!!, userUID, matchID!!, matchDate!!, matchTime!!,
                            teamName!!, radioText)
                        upComingDetailsViewModel.restoreMatch(uid!!, matchID!!, deviceToken!!, teamName!!, teamPhone!!, matchDate!!, matchTime!!,
                            matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!, locationAddress!!, lat!!, long!!, geoHash!!)
                    }
                    val timeUtils : Long = System.currentTimeMillis()
                    upComingDetailsViewModel.canceUpComingListNotification(clientUID!!, teamName!!, userImageUrl!!, "cancelUpComing", matchDate!!, matchTime!!, radioText, timeUtils)
                    dialog.dismiss()
                }else{
                    Toast.makeText(this,"Vui lòng chọn lí do",Toast.LENGTH_LONG).show()
                }
            }
            cancelMatchDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun chat() {
        activityUpcomingdetailsBinding.openChat.setOnClickListener {
            val intent = Intent(this, ChatDetailsActivity::class.java)
            intent.putExtra("team", clientTeamName)
            intent.putExtra("userUid", clientUID)
            startActivity(intent)
            Animation.animateSlideLeft(this)
        }
    }

    private fun back() {
        activityUpcomingdetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun phoneCall() {
        activityUpcomingdetailsBinding.phoneCall.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )
            } else {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${clientPhone}"))
                startActivity(intent)
                Animation.animateSlideLeft(this)
            }
        }
    }


    private fun handleVariable() {
        FirebaseDatabase.getInstance().getReference("Teams").child(userUID!!).get()
            .addOnSuccessListener {
                teamName = it.child("teamName").value.toString()
            }
        FirebaseDatabase.getInstance().getReference("Users").child(clientUID!!).get()
            .addOnSuccessListener {
                clientPhone = it.child("teamName").value.toString()
            }
        StorageConnection.storageReference.getReference("Users").child(userUID!!).downloadUrl
            .addOnSuccessListener {
                userImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
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
                        Model.currentLat = list[0].latitude
                        Model.currentLong = list[0].longitude
                        Model.currentAddress = list[0].getAddressLine(0)
                        Log.e("Address", Model.currentAddress.toString())
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

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled (
            LocationManager.NETWORK_PROVIDER
        )
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

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(activityUpcomingdetailsBinding) {
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

                if (userUID == data?.userUID) {
                    Glide.with(teamImage).load(data?.clientImageUrl).centerCrop().into(teamImage)
                    teamName.text = data?.clientTeamName
                } else {
                    Glide.with(teamImage).load(data?.teamImageUrl).centerCrop().into(teamImage)
                    teamName.text = data?.teamName
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
                Model.click = data?.click
                clientTeamName = data?.clientTeamName
                Model.clientImageUrl = data?.clientImageUrl
                clientUID = data?.clientUID
                destinationLat = data?.lat
                destinationLong = data?.long
                destinationAddress = data?.locationAddress
                uid = data?.userUID
                geoHash = data?.geoHash
            }
        }
    }

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data: CreateMatchModel?) {
            context.startActivity(Intent(context, ActivityUpcomingDetails::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
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

}