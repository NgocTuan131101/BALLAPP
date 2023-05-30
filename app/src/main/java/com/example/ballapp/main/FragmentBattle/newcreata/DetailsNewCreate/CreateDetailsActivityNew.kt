package com.example.ballapp.main.FragmentBattle.newcreata.DetailsNewCreate

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
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.ballapp.R
import com.example.ballapp.databinding.ActivityCreateDetailsNewBinding
import com.example.ballapp.databinding.SignOutDialogBinding
import com.example.ballapp.databinding.SuccessDialogBinding
import com.example.ballapp.utils.Animation
import com.example.ballapp.utils.Model
import com.example.ballball.model.CreateMatchModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class CreateDetailsActivityNew: AppCompatActivity() {
    private lateinit var activityCreateDetailsNewBindingbinding: ActivityCreateDetailsNewBinding
    private val CreateDatailsViewmodel: CreateDatailsViewmodel by viewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var signOutDialogBinding: SignOutDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val permissionId = 3
    private lateinit var successDialogBinding: SuccessDialogBinding

    companion object {
        private const val NEW_CREATE_DATA = "newCreate_data"
        fun startDetails(context: Context, data: CreateMatchModel?) {
            context.startActivity(Intent(context, CreateDetailsActivityNew::class.java).apply {
                putExtra(NEW_CREATE_DATA, data)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreateDetailsNewBindingbinding =
            ActivityCreateDetailsNewBinding.inflate(layoutInflater)
        setContentView(activityCreateDetailsNewBindingbinding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        initEvents()
        initObserve()


    }

    private fun initObserve() {
        cancelMatchObserve()
    }

    /*

     */
    private fun cancelMatchObserve() {
        CreateDatailsViewmodel.deleteNewCreate.observe(this) { result ->
            when (result) {
                is CreateDatailsViewmodel.DeleteNewCreate.ResultOk -> {
                    val dialog = Dialog(this, R.style.MyTimePickerTheme)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    successDialogBinding = SuccessDialogBinding.inflate(layoutInflater)
                    dialog.setContentView(successDialogBinding.root)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    successDialogBinding.text.text = "Trận đấu này của bạn đã được xóa"
                    successDialogBinding.successLayout.setOnClickListener {
                        dialog.dismiss()
                        finish()
                        Animation.animateSlideRight(this)
                    }
                    dialog.show()
                }
                is CreateDatailsViewmodel.DeleteNewCreate.ResultError -> {}
            }
        }
    }

    private fun initEvents() {
        binding()
        back()
        cancelMatch()
        openMap()

    }

    private fun openMap() {
        activityCreateDetailsNewBindingbinding.navigationLayout.setOnClickListener {

        }
    }

    private fun cancelMatch() {
        activityCreateDetailsNewBindingbinding.cancelMatch.setOnClickListener {
            val dialog = Dialog(this, R.style.MyTimePickerTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            signOutDialogBinding = SignOutDialogBinding.inflate(layoutInflater)
            dialog.setContentView(signOutDialogBinding.root)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            signOutDialogBinding.title.text = "Xóa trận"
            signOutDialogBinding.content.text = "Bạn có muốn xóa trận đấu này không ?"
            signOutDialogBinding.yes.setOnClickListener {
                if (userUID != null) {
                    CreateDatailsViewmodel.deleteNewCreate(userUID, Model.matchID!!)
                }
                dialog.dismiss()
            }
            signOutDialogBinding.no.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(NEW_CREATE_DATA)
            with(activityCreateDetailsNewBindingbinding) {
                if (data?.click == 0) {
                    clickLayout.visibility = View.GONE
                }
                teamName.text = data?.teamName
                Glide.with(teamName).load(data?.teamImageUrl).centerCrop().into(teamImage)
                clickNumber.text = data?.click.toString()
                date.text = data?.date
                time.text = data?.time
                peopleNumber.text = data?.teamPeopleNumber
                location.text = data?.locationAddress
                if (data?.note?.isEmpty() == true) {
                    note.text = "..."
                } else {
                    note.text = data?.note
                }
                Model.matchID = data?.matchID
                Model.destinationLat = data?.lat
                Model.destinationLong = data?.long
                Model.destinationAddress = data?.locationAddress

            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(
            LocationManager
                .GPS_PROVIDER
        ) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this, arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ),
            permissionId)
    }
    private fun checkPermissions():Boolean{
        if(ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                        task ->
                    val location: Location? =task.result
                    if(location != null){
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list:kotlin.collections.List<Address> =
                            geocoder.getFromLocation(location.latitude,location.longitude,1) as List<Address>
                        Model.currentLat = list[0].latitude
                        Model.currentLong = list[0].longitude
                        Model.currentAddress = list[0].getAddressLine(0)
                        Log.e("Address", Model.currentAddress.toString())
                    }

                }
            }else{
                Toast.makeText(this,"Hãy bật định vị của bạn", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        }else {
            requestPermissions()
        }

    }

    private fun back() {
        activityCreateDetailsNewBindingbinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}