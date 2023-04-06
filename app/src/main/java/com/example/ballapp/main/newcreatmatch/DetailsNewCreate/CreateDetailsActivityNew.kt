package com.example.ballapp.main.newcreatmatch.DetailsNewCreate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ballapp.R
import com.example.ballapp.databinding.ActivityCreateDetailsNewBinding
import com.example.ballball.model.CreateMatchModel

class CreateDetailsActivityNew : AppCompatActivity() {
    private lateinit var activityCreateDetailsNewBindingbinding: ActivityCreateDetailsNewBinding

    companion object {
        private const val NEW_CREATE_DATA = "newCreate_data"
        fun startDetails(context: Context, data : CreateMatchModel?)
        {
            context.startActivity(Intent(context, CreateDetailsActivityNew::class.java).apply {
                putExtra(NEW_CREATE_DATA, data)
            })
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_create_details_new)

        }
}