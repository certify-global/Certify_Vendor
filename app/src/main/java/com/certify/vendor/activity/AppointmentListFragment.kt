package com.certify.vendor.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
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
    private var textviewscheduleAppoinment: TextView? = null
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
        setOnClickListener()
        Utils.enableBluetooth()
        setOnBackPress()
    }

    private fun setOnClickListener() {
        textviewscheduleAppoinment?.setOnClickListener {
            findNavController().navigate(R.id.scheduleFragment)
        }
    }

    private fun initView() {
        progressIndicator = appointView.findViewById(R.id.progress_indicator)
        val userName: TextView? = appointView.findViewById(R.id.appt_user_name)
        textviewscheduleAppoinment = appointView.findViewById(R.id.textview_scheduleAppoinment)
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



        private fun setBadgeUI(statDate: String, endDate: String, appointmentStatus: Int, vendorGuid: String) = try {
        val badgeUILayout: ConstraintLayout = appointView.findViewById(R.id.badge_screen)
        val badgeLayout = appointView.findViewById<LinearLayout>(R.id.rl_badge_status)
        val userImage: ImageView? = appointView.findViewById(R.id.img_user_badge)
        val QRCodeImage: ImageView? = appointView.findViewById(R.id.img_qa_badge)
        val companyName: TextView? = appointView.findViewById(R.id.tv_company_name)
        val badgeId: TextView? = appointView.findViewById(R.id.tv_id_badge)
        val userName: TextView? = appointView.findViewById(R.id.tv_user_name_badge)
        val validity: TextView? = appointView.findViewById(R.id.tv_expires_badge)
        val timeStamp: TextView? = appointView.findViewById(R.id.tv_appt_badge)
        //val inactive: ImageView? = appointView.findViewById(R.id.img_inactive_badge)
        if (appointmentStatus == 3) {
            //inactive?.visibility = View.VISIBLE
            badgeLayout.visibility = View.GONE
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_END_TIME, "")

        } else {
            //inactive?.visibility = View.GONE
            badgeLayout.visibility = View.VISIBLE
            val userPicStr = AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
            if (userPicStr.isNotEmpty()) userImage?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
            badgeId?.text = String.format("%s%s", getString(R.string.id), AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID))
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_END_TIME, endDate)
            QRCodeImage?.setImageBitmap(Utils.QRCodeGenerator(AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_GUID), 150, 150))

            companyName?.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
            userName?.text = String.format(
                getString(R.string.badge_user_name),
                AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
                AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
            )
            val dateStr = endDate.let { Utils.getDate(it, "MM/dd/yyyy") }
            validity?.text = String.format(getString(R.string.expires), dateStr)
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_DATE, dateStr)
            val apptTime = Utils.getTime(statDate) + "-" + Utils.getTime(endDate)
            val timeStampStr = context?.getString(R.string.appointment_status)?.let {
                String.format(it, apptTime)
            }
            timeStamp?.text = timeStampStr
            AppSharedPreferences.writeSp((AppSharedPreferences.getSharedPreferences(context)), Constants.APPOINT_TIME, apptTime)
        }
        BadgeController.getInstance().convertUIToImage(badgeUILayout, context)
    } catch (e: Exception) {
        e.printStackTrace()
    }


    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val bestProvider = lm.getBestProvider(criteria, false)
        if (bestProvider != null) {
            val location = lm.getLastKnownLocation(bestProvider!!)
            if (location == null) {
                //   Toast.makeText(activity, "Location Not found", Toast.LENGTH_LONG).show()
            } else {
                val geocoder = Geocoder(activity)
                try {
                    val user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    userLocation?.latitude = user.get(0).latitude;
                    userLocation?.longitude = user.get(0).longitude;
                    Log.i("getUserLocation", "" + user.get(0).latitude + "," + user.get(0).longitude)
                    userLocation?.let { it1 -> AppointmentController.getInstance()?.setAppointmentLocation(it1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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