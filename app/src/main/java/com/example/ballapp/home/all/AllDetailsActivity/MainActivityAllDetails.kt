package com.example.ballapp.home.all.AllDetailsActivity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ballapp.R
import com.example.ballapp.databinding.ActivityMainAllDetailsBinding
import com.example.ballapp.utils.Animation
import com.example.ballball.model.CreateMatchModel

class MainActivityAllDetails : AppCompatActivity() {
    private lateinit var mainActivityAllDetails :ActivityMainAllDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_all_details)
        initEvents()
        initObserve()
    }

    private fun initObserve() {

    }

    private fun initEvents() {
        back()
        binding()
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<CreateMatchModel>(KEY_DATA)
            with(mainActivityAllDetails) {
                if (data?.click == 0) {

                }
            }
        }
    }

    private fun back() {
        mainActivityAllDetails.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }


    }

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, MainActivityAllDetails::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

}