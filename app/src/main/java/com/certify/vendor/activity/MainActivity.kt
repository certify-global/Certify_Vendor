package com.certify.vendor.activity

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.appcompat.app.AppCompatActivity
import com.certify.vendor.R
import com.certify.vendor.VendorApplication
import com.certify.vendor.common.Constants
import com.certify.vendor.data.AppSharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        if (AppSharedPreferences.readSp(AppSharedPreferences.getSharedPreferences(this), Constants.IS_LOGGEDIN)) {
            launchAppointmentActivity()
        }
    }

    fun launchAppointmentActivity() {
        startActivity(Intent(this, AppointmentActivity::class.java))
    }
}