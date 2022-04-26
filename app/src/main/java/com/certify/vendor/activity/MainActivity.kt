package com.certify.vendor.activity

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
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

    override fun onResume() {
        super.onResume()
        if (AppSharedPreferences.readSp(AppSharedPreferences.getSharedPreferences(this), Constants.IS_LOGGEDIN)) {
            finish()
            launchAppointmentActivity()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun launchAppointmentActivity() {
        startActivity(Intent(this, AppointmentActivity::class.java))
    }
}