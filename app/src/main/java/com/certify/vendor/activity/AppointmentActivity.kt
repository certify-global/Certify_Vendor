package com.certify.vendor.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.certify.vendor.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.navigation.NavigationBarView

class AppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.nav_host_appointment).navigate(R.id.appointmentListFragment)
        setNavigationMenu()
    }

    private fun setNavigationMenu() {
        val navigationView : BottomNavigationView = findViewById(R.id.navigation_menu_view)
        navigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                Log.d("AppointmentActivity", "Menu item ${item.title}" )
                if (item.itemId == R.id.menu_badge) {
                    findNavController(R.id.nav_host_appointment).navigate(R.id.action_appointmentListFragment_to_badgeFragment)
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