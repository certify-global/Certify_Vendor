package com.certify.vendor.activity

import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2
import com.certify.vendor.R
import com.certify.vendor.adapter.AppointmentTypeAdapter
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.controller.AppointmentController
import com.certify.vendor.data.AppSharedPreferences
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AppointmentListFragment : BaseFragment() {


    private var appointTab: TabLayout? = null
    private var viewPager: ViewPager2? = null
    private var sharedPreferences: SharedPreferences? = null
    private var userLocation: Location? = Location("")
    //private lateinit var badgeViewDevice: View
    private lateinit var appointView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appointView = inflater.inflate(R.layout.fragment_appointment, container, false)
        return appointView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { it ->
            AppointmentController.getInstance()?.initAppointment(
                it
            )
        }
      //  getUserLocation()
        sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        initView()
        Utils.enableBluetooth()
        setOnBackPress()
    }

    private fun initView() {
        progressIndicator = appointView.findViewById(R.id.progress_indicator)
        val userName: TextView? = appointView.findViewById(R.id.appt_user_name)
        userName?.text = String.format(getString(R.string.appt_user_name), AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME))
        String.format(getString(R.string.appt_user_name), AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME))
        val userPic: ImageView? = appointView.findViewById(R.id.user_profile_pic)
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty()) {
            userPic?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        }
        val appointment = arrayOf(
            getString(R.string.upcoming),
            getString(R.string.past),
            getString(R.string.expired)
        )
        appointTab = appointView.findViewById(R.id.appointment_tab)
        viewPager = appointView.findViewById(R.id.appointment_viewPager)
        appointTab?.newTab()?.let { appointTab?.addTab(it.setText(getString(R.string.upcoming))) }
        appointTab?.newTab()?.let { appointTab?.addTab(it.setText(getString(R.string.past))) }
        appointTab?.newTab()?.let { appointTab?.addTab(it.setText(getString(R.string.expired))) }
        appointTab?.setTabGravity(TabLayout.GRAVITY_FILL);
        val appointmentTypeAdapter = AppointmentTypeAdapter(childFragmentManager, lifecycle)
        viewPager?.adapter = appointmentTypeAdapter
        TabLayoutMediator(appointTab!!, viewPager!!) { tab, position ->
            tab.text = appointment[position]
        }.attach()


    }
    private fun setOnBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override
            fun handleOnBackPressed() {
                BadgeController.getInstance().onBadgeClose()
                BadgeController.getInstance().isBadgeDisconnected = false
                val sharedPreferences = AppSharedPreferences.getSharedPreferences(activity)
                AppSharedPreferences.writeSp(sharedPreferences, Constants.BADGE_DEVICE_UPDATED, false)
                BadgeController.getInstance().unRegisterReceiver()
                activity?.finish()
            }
        })
    }
}