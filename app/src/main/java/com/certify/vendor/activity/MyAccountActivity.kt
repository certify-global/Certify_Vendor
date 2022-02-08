package com.certify.vendor.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.certify.vendor.R
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.databinding.ActivityAccountBinding

class MyAccountActivity : AppCompatActivity() {
    private val TAG = MyAccountActivity::class.java.name
    private var sharedPreferences: SharedPreferences? = null
    var activityAccountBinding:ActivityAccountBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAccountBinding = DataBindingUtil.setContentView(this,R.layout.activity_account)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(this)
        setClickListener()
        updateUI()
    }

    private fun updateUI() {
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty()) {
            activityAccountBinding?.imageViewUser?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        }
        activityAccountBinding?.editTextName?.setText(AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME)+" " +   AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME))
        activityAccountBinding?.editTextEmail?.setText(AppSharedPreferences.readString(sharedPreferences, Constants.USER_EMAIL))
    }

    private fun setClickListener() {
        activityAccountBinding?.imgBack?.setOnClickListener { finish() }
    }
}