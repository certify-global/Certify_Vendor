package com.certify.vendor.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.certify.vendor.R
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppointmentDataSource
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView

class AppointmentActivity : AppCompatActivity() {
    private var floatingActionButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
        Utils.permissionCheck(this)
        initView()
        setClickListener()
    }

    private fun setClickListener() {
        floatingActionButton?.setOnClickListener {
            floatingActionButton?.isEnabled = false
            AppointmentDataSource.isSchedule = true
            startActivity(Intent(this, ScheduleActivity::class.java))
            ///     findNavController(R.id.nav_host_appointment).navigate(R.id.scheduleFragment)
        }
    }

    private fun initView() {
        floatingActionButton = findViewById(R.id.floatingActionButton)
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.nav_host_appointment).navigate(R.id.appointmentListFragment)
        setNavigationMenu()
    }

    private fun checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        }
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), 1000)
        }
    }

    private fun setNavigationMenu() {
        val navigationView: BottomNavigationView = findViewById(R.id.navigation_menu_view)
        navigationView.selectedItemId = R.id.menu_home
        navigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                findNavController(R.id.nav_host_appointment).popBackStack()
                when (item.itemId) {
                    R.id.menu_badge -> {
                        findNavController(R.id.nav_host_appointment).navigate(R.id.badgeFragment)
                    }
                    R.id.menu_home -> {
                        findNavController(R.id.nav_host_appointment).navigate(R.id.appointmentListFragment)

                    }
                    R.id.menu_settings -> {
                        findNavController(R.id.nav_host_appointment).navigate(R.id.SettingsFragment)

                    }
                    R.id.menu_credentials -> {
                        findNavController(R.id.nav_host_appointment).navigate(R.id.CredentialsFragment)

                    }
                }
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        floatingActionButton?.isEnabled = true

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}