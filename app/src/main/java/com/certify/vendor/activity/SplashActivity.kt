package com.certify.vendor.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.certify.vendor.R
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setSplashScreenTimer()
    }

    private fun setSplashScreenTimer() {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                finish()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        }, 5 * 1000)
    }
}