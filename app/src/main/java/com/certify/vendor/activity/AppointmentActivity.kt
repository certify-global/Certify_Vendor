package com.certify.vendor.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.certify.vendor.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView

class AppointmentActivity : AppCompatActivity() {
    private var floatingActionButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
        initView();
        setClickListener();
    }

    private fun setClickListener() {
     floatingActionButton?.setOnClickListener {
         findNavController(R.id.nav_host_appointment).navigate(R.id.scheduleFragment)

     }
    }

    private fun initView() {
      floatingActionButton=findViewById(R.id.floatingActionButton)
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.nav_host_appointment).navigate(R.id.appointmentListFragment)
        setNavigationMenu()
    }

    private fun setNavigationMenu() {
        val navigationView: BottomNavigationView = findViewById(R.id.navigation_menu_view)
        navigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                Log.d("AppointmentActivity", "Menu item ${item.title}")
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

    private fun launchActivity() {
        startActivity(Intent(this, BadgeActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}