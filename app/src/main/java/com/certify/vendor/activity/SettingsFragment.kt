package com.certify.vendor.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.certify.vendor.R
import com.certify.vendor.data.AppSharedPreferences

class SettingsFragment : Fragment() {

    private val TAG = SettingsFragment::class.java.name
    private lateinit var badgeView: View
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        badgeView = inflater.inflate(R.layout.settings_layout, container, false)
        return badgeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)

    }

    override fun onStart() {
        super.onStart()
        //BadgeController.getInstance().init(context)
    }
}