package com.example.ballapp.home.all.AllDetailsActivity

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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.ballapp.R
import com.example.ballapp.databinding.ActivityMainAllDetailsBinding
import com.example.ballapp.databinding.SignOutDialogBinding
import com.example.ballapp.databinding.SuccessDialogBinding
import com.example.ballapp.utils.Animation
import com.example.ballapp.utils.Model
import com.example.ballapp.utils.Model.click
import com.example.ballapp.utils.Model.clientImageUrl
import com.example.ballapp.utils.Model.destinationAddress
import com.example.ballapp.utils.Model.destinationLat
import com.example.ballapp.utils.Model.destinationLong
import com.example.ballapp.utils.Model.deviceToken
import com.example.ballapp.utils.Model.geoHash
import com.example.ballapp.utils.Model.matchDate
import com.example.ballapp.utils.Model.matchLocation
import com.example.ballapp.utils.Model.matchTime
import com.example.ballapp.utils.Model.teamConfirmUID
import com.example.ballapp.utils.Model.teamImageUrl
import com.example.ballapp.utils.Model.teamNote
import com.example.ballapp.utils.Model.teamPeopleNumber
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
class MainActivityAllDetails : AppCompatActivity() {
    private lateinit var mainActivityAllDetails: ActivityMainAllDetailsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private lateinit var successDialogBinding: SuccessDialogBinding
    private val allDetailsViewModel: AllDetailsViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val permissionId = 3
    var name: String? = null
    var phomeNumber: String? = null
    var click: Int? = null
    var matchID: String? = null
    var clientTeamName: String? = null
    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data: CreateMatchModel?) {
            context.startActivity(Intent(context, MainActivityAllDetails::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivityAllDetails = ActivityMainAllDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mainActivityAllDetails.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
        initObserve()
    }

    private fun initObserve() {
        cachMatchOb()
        saveWaitMatchListNotificaationOb()
    }

    private fun saveWaitMatchListNotificaationOb() {
        allDetailsViewModel.waitMatchListNotification.observe(this){result ->
            when(result){
                is AllDetailsViewModel.WaitMatchListNotificationResult.ResultOk ->{}
                is AllDetailsViewModel.WaitMatchListNotificationResult.ResultError ->{}
            }
        }
    }

    private fun initEvents() {
        back()
        binding()
        handleVariable()
        catchMatch()
        chat()
        phoneCall()
    }

    private fun phoneCall() {
        mainActivityAllDetails.phoneCall.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),1)
            }
            else{
                val  intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phomeNumber"))
                startActivity(intent)
                Animation.animateSlideLeft(this)
            }
        }
    }

    private fun chat() {
        mainActivityAllDetails.openChat.setOnClickListener {

        }
    }

    private fun catchMatch() {
        mainActivityAllDetails.catchMatch.setOnClickListener {
            val dialog = Dialog(this,R.style.MyTimePickerTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Bắt trận"
            signOutDialogBinding.content.text = "Bạn có muốn bắt trận với $name không ?"
            signOutDialogBinding.yes.setOnClickListener {
                click = Model.click?.plus(1)
                val clientUID = "clienUID$click"
                matchID?.let { matchID ->
                    if(userUID != null){
                        click?.let { click ->
                            allDetailsViewModel.handleCatchMatch(userUID, userUID,teamConfirmUID!!,
                            matchID, deviceToken!!,name!!,phomeNumber!!, matchDate!!, matchTime!!,
                                matchLocation!!, teamNote!!, teamPeopleNumber!!, teamImageUrl!!,
                                destinationAddress!!, destinationLat!!, destinationLong!!,click,clientTeamName!!,clientUID,
                            clientImageUrl!!,userUID,
                                teamConfirmUID!!,
                                geoHash!!,click)
                            val timeUtils :Long = System.currentTimeMillis()
                            allDetailsViewModel.waiMatchListNotification(teamConfirmUID!!,clientTeamName!!,
                                userImageUrl!!,"waitMatch",
                                matchDate!!,
                                matchTime!!,
                            timeUtils)
                        }
                    }
                }
                dialog.dismiss()
            }
            signOutDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun cachMatchOb() {
        allDetailsViewModel.catchMatch.observe(this) { result ->
            when (result) {
                is AllDetailsViewModel.CatchMatch.ResultOK ->{
                    val dialog = Dialog(this,R.style.MyTimePickerTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Yêu cầu của bạn đã được gửi, chờ $name xác nhân"
                    successDialogBinding.next.setOnClickListener {
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
                    },5000)
                }
                is AllDetailsViewModel.CatchMatch.ResultError -> {}
                is AllDetailsViewModel.CatchMatch.WaitMatchOK -> {}
                is AllDetailsViewModel.WaitMatchListNotificationResult -> {}
                else -> {}
            }
        }
    }


    private fun handleVariable() {
        FirebaseDatabase.getInstance().getReference("Teams").child(userUID!!).get()
            .addOnSuccessListener {
                clientTeamName = it.child("teamName").value.toString()
            }
        StorageConnection.storageReference.getReference("Teams").child(userUID!!).downloadUrl
            .addOnSuccessListener {
                clientImageUrl = it.toString()
            }
            .addOnFailureListener {
                Log.e("Error", it.toString())
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
            with(mainActivityAllDetails) {
                if (data?.click == 0) {
                    clickLayout.visibility = View.GONE
                }
                teamName.text = data?.teamName
                Glide.with(teamImage).load(data?.teamImageUrl).centerCrop().into(teamImage)
                clickNumber.text = data?.click.toString()
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
                destinationLat = data?.lat
                destinationLong = data?.long
                destinationAddress = data?.locationAddress
                phomeNumber = data?.teamPhone
                name = data?.teamName
                click = data?.click
                matchID = data?.matchID
                deviceToken = data?.deviceToken
                matchDate = data?.date
                matchTime = data?.time
                teamNote = data?.note
                teamPeopleNumber = data?.teamPeopleNumber
                teamImageUrl = data?.teamImageUrl
                matchLocation = data?.location
                teamConfirmUID = data?.userUID
                geoHash = data?.geoHash

            }
        }
    }

    private fun back() {
        mainActivityAllDetails.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionId
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(
            LocationManager
                .GPS_PROVIDER
        ) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

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
                            geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            ) as List<Address>
                        Model.currentLat = list[0].latitude
                        Model.currentLong = list[0].longitude
                        Model.currentAddress = list[0].getAddressLine(0)
                        Log.e("Address", Model.currentAddress.toString())
                    }

                }
            } else {
                Toast.makeText(this, "Hãy bật định vị của bạn", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

    }

}