package com.certify.vendor.activity

import android.Manifest
import android.app.Activity
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.certify.vendor.R
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.databinding.FragmentBadgeBinding
import com.certify.vendor.model.BadgeViewModel

class BadgeFragment : Fragment() {

    private val TAG = BadgeFragment::class.java.name
    private lateinit var badgeFragmentBinding: FragmentBadgeBinding
    private var badgeViewModel: BadgeViewModel? = null
    private var sharedPreferences: SharedPreferences? = null
    private var container: ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        badgeFragmentBinding = FragmentBadgeBinding.inflate(inflater, container, false)
        badgeFragmentBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = badgeViewModel
        }
        badgeViewModel = ViewModelProvider(this).get(BadgeViewModel::class.java)
        this.container = container
        return badgeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
            setUserProfile()
            setQrCodeImage()
            setAppointmentStatus()
            Utils.enableBluetooth()
            if(Utils.permissionCheckBluetooth(context))
                setBadgeStatus()
            else {
                val permissionList = arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
                ActivityCompat.requestPermissions((context as Activity?)!!, permissionList, Utils.PERMISSION_REQUEST_CODE)
            }
            setOnBackPress()
        } catch (e: Exception) {
            Log.e(TAG, "Exception in setting the badge UI" + e.message)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            onBadgeConnectionStatus()
        } catch (e: Exception) {
            Log.e(TAG, "onResume" + e.message)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //badgeViewModel?.onClose()
    }

    private fun setUserProfile() {
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty())
            badgeFragmentBinding.badgeUserImage.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        badgeFragmentBinding.badgeUserName.text = String.format(getString(R.string.badge_user_name), AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME), AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME))
        badgeFragmentBinding.badgeId.text = String.format("%s%s", getString(R.string.id), AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID))
        badgeFragmentBinding.badgeStatus.text = getString(R.string.active)
        badgeFragmentBinding.badgeCompanyName.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
        badgeFragmentBinding.badgeExpires.text = String.format(getString(R.string.expires), AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_EXPIRY_MM_DD_YY))
    }

    private fun setQrCodeImage() {
        val qrCodeImage = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_GUID)
        if (qrCodeImage.isNotEmpty()) {
            badgeFragmentBinding.badgeQrCode.setImageBitmap(Utils.QRCodeGenerator(qrCodeImage, 340, 340))
        }
    }

    private fun setAppointmentStatus() {
        badgeFragmentBinding.badgeAppt.text = String.format(
            getString(R.string.appt),
            AppSharedPreferences.readString(sharedPreferences, Constants.APPOINT_TIME)
        )
    }

    private fun setBadgeStatus() {
        badgeViewModel?.init(context)
        onBadgeConnectionStatus()
        setBadgeBattery()
        badgeViewModel?.badgeAvailable?.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, getString(R.string.badge_unavailable), Toast.LENGTH_SHORT).show()
            }
        }
        onManageBadge()
    }

    private fun onBadgeConnectionStatus() {
        try {
          //  if (!Utils.permissionCheckBluetooth(context))
            onBadgeConnectionStatusUpdate(BadgeController.getInstance().connectionState.value)
            badgeViewModel?.badgeConnectionStatus?.value = BadgeController.BadgeConnectionState.NOT_CONNECTED.value
            badgeViewModel?.badgeConnectionStatus?.observe(viewLifecycleOwner) {
                onBadgeConnectionStatusUpdate(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "onBadgeConnectionStatus " + e.message)
        }
    }

    private fun onBadgeConnectionStatusUpdate(connectionStatus: Int) {
        when (connectionStatus) {
            BadgeController.BadgeConnectionState.CONNECTED.value -> {
                badgeFragmentBinding.badgeConnectionImage.setImageResource(R.drawable.ic_badge_connected)
                badgeFragmentBinding.badgeConnectionStatus.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.green_1))
                badgeFragmentBinding.badgeConnectionStatus.text = getString(R.string.badge_connected)
            }
            BadgeController.BadgeConnectionState.DISCONNECTED.value -> {
                badgeFragmentBinding.badgeConnectionImage.setImageResource(R.drawable.ic_badge_disconnected)
                badgeFragmentBinding.badgeConnectionStatus.setTextColor(ContextCompat.getColor(this.requireContext(), R.color.red))
                badgeFragmentBinding.badgeConnectionStatus.text = getString(R.string.badge_disconnected)
                setBadgeDeviceUI()
            }
        }
    }

    private fun onGetBattery(batteryLevel: Int) {
        badgeFragmentBinding.batteryStatusLayout.visibility = View.VISIBLE

        when (batteryLevel) {
            BadgeController.BatteryLevel.COMPLETE.value,
            BadgeController.BatteryLevel.HIGH.value -> {
                badgeFragmentBinding.batteryStatus.setImageResource(R.drawable.ic_battery_high)
                badgeFragmentBinding.batteryStatusLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.blue))
                badgeFragmentBinding.batteryPercent.text = getString(R.string.badge_percent_100)
            }
            BadgeController.BatteryLevel.MEDIUM.value -> {
                badgeFragmentBinding.batteryStatus.setImageResource(R.drawable.ic_battery_medium)
                badgeFragmentBinding.batteryStatusLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.light_green))
                badgeFragmentBinding.batteryPercent.text = getString(R.string.badge_percent_70)
            }
            BadgeController.BatteryLevel.LOW_POWER.value -> {
                badgeFragmentBinding.batteryStatus.setImageResource(R.drawable.ic_battery_low)
                badgeFragmentBinding.batteryStatusLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.orange))
                badgeFragmentBinding.batteryPercent.text = getString(R.string.badge_percent_30)
            }
            BadgeController.BatteryLevel.TOO_LOW.value -> {
                badgeFragmentBinding.batteryStatus.setImageResource(R.drawable.ic_battery_too_low)
                badgeFragmentBinding.batteryStatusLayout.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.red))
                badgeFragmentBinding.batteryPercent.text = getString(R.string.badge_percent_10)
            }
        }
        updateBadgeDeviceUI()
    }

    private fun onManageBadge() {
        badgeFragmentBinding.manageBadge.setOnClickListener {
            //badgeViewModel?.onClose()
            findNavController().navigate(R.id.badgeManageFragment)
        }
    }

    private fun setBadgeBattery() {
        try {
            val batteryLevel = sharedPreferences?.getInt(Constants.BADGE_BATTERY_STATUS, -1)
            if (batteryLevel == -1) {
                badgeViewModel?.getBattery()
                badgeViewModel?.batteryLevel?.observe(viewLifecycleOwner) {
                    onGetBattery(it)
                }
            } else {
                if (!BadgeController.getInstance().isBadgeDisconnected) {
                    batteryLevel?.let { onGetBattery(it) }
                } else {
                    onGetBattery(batteryLevel!!)
                }
            }
        } catch (e: Exception) {

        }

    }

    private fun setBadgeDeviceUI() {
        val view = layoutInflater.inflate(R.layout.badge_device_layout, container, false)
        val userImage: ImageView? = view?.findViewById(R.id.img_user_badge)
        val QRCodeImage: ImageView? = view?.findViewById(R.id.img_qa_badge)
        val companyName: TextView? = view?.findViewById(R.id.tv_company_name)
        val badgeId: TextView? = view?.findViewById(R.id.tv_id_badge)
        val userName: TextView? = view?.findViewById(R.id.tv_user_name_badge)
        val validity: TextView? = view?.findViewById(R.id.tv_expires_badge)
        val apptTime: TextView? = view?.findViewById(R.id.tv_appt_badge)
        apptTime?.visibility = View.GONE
        validity?.visibility = View.GONE
        val userPicStr = AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty()) userImage?.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))
        badgeId?.text = String.format("%s%s", getString(R.string.id), AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID))

        val vendorGuid = sharedPreferences?.getString(Constants.VENDOR_GUID, "")
        if (vendorGuid?.isNotEmpty() == true) {
            QRCodeImage?.setImageBitmap(Utils.QRCodeGenerator(vendorGuid, 150, 150))
        }

        companyName?.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
        userName?.text = String.format(
            getString(R.string.badge_user_name),
            AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
            AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
        )
        BadgeController.getInstance().convertUIToImage(view as ConstraintLayout?, context)
        AppSharedPreferences.writeSp(sharedPreferences, Constants.BADGE_DEVICE_UPDATED, true)
    }

    private fun updateBadgeDeviceUI() {
        val badgeDeviceUpdated = sharedPreferences?.getBoolean(Constants.BADGE_DEVICE_UPDATED, false)
        if (!badgeDeviceUpdated!!) {
            BadgeController.getInstance().disconnectDevice()
        }
    }

    private fun setOnBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override
            fun handleOnBackPressed() {
                badgeViewModel?.onClose()
                BadgeController.getInstance().isBadgeDisconnected = false
                val sharedPreferences = AppSharedPreferences.getSharedPreferences(activity)
                AppSharedPreferences.writeSp(sharedPreferences, Constants.BADGE_DEVICE_UPDATED, false)
                BadgeController.getInstance().unRegisterReceiver()
                activity?.finish()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isGrant = false
        for (item in grantResults.iterator()) {
            Log.i(TAG, "onRequestPermissionsResult -> item = " + item)
            isGrant = item == 200
        }
        if (isGrant)
            setBadgeStatus()
    }
}