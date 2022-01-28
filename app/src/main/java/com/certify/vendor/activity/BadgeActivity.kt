package com.certify.vendor.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.certify.vendor.R
import com.certify.vendor.badge.Badge
import com.certify.vendor.badge.BadgeController

class BadgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.nav_host_badge).navigate(R.id.badgeFragment)
    }
}