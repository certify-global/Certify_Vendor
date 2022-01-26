package com.certify.vendor.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.certify.vendor.R

class AppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.nav_host_fragment).navigate(R.id.appointmentListFragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}