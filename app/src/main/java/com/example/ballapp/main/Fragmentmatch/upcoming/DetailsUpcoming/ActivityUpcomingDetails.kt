package com.example.ballapp.main.Fragmentmatch.upcoming.DetailsUpcoming

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.ballapp.R
import com.example.ballapp.databinding.ActivityUpcomingdetailsBinding
import com.example.ballapp.databinding.CancelMatchDialogBinding
import com.example.ballapp.databinding.DialogSuccessBinding
import com.example.ballball.model.CreateMatchModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityUpcomingDetails : AppCompatActivity() {
    private lateinit var activityUpcomingdetailsBinding: ActivityUpcomingdetailsBinding
    private val upComingDetailsViewModel : UpComingDetailsViewModel by viewModels()
    private lateinit var cancelMatchDialogBinding: CancelMatchDialogBinding
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var successBinding: DialogSuccessBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionId = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUpcomingdetailsBinding = ActivityUpcomingdetailsBinding.inflate(layoutInflater)
        setContentView(activityUpcomingdetailsBinding.root)
    }
    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, ActivityUpcomingDetails::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }
}