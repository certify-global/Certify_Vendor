package com.certify.vendor.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.certify.vendor.R

class AppointmentListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}