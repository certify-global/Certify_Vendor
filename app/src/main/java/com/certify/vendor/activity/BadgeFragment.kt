package com.certify.vendor.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var badgeFragmentBinding : FragmentBadgeBinding
    private var badgeViewModel : BadgeViewModel? = null
    private var sharedPreferences: SharedPreferences? = null

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
        return badgeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
            setUserProfile()
            setQrCodeImage()
            setAppointmentStatus()
            setBadgeStatus()
        } catch (e: Exception) {
            Log.e(TAG, "Exception in setting the badge UI")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        badgeViewModel?.onClose()
    }

    private fun setUserProfile() {
        val userPicStr =
            AppSharedPreferences.readString(sharedPreferences, Constants.USER_PROFILE_PIC)
        if (userPicStr.isNotEmpty())
            badgeFragmentBinding.badgeUserImage.setImageBitmap(Utils.decodeBase64ToImage(userPicStr))

        badgeFragmentBinding.badgeUserName.text = String.format(
            getString(R.string.badge_user_name),
            AppSharedPreferences.readString(sharedPreferences, Constants.FIRST_NAME),
            AppSharedPreferences.readString(sharedPreferences, Constants.LAST_NAME)
        )

        badgeFragmentBinding.badgeId.text = String.format(
            "%s%s",
            getString(R.string.id),
            AppSharedPreferences.readString(sharedPreferences, Constants.BADGE_ID)
        )
        badgeFragmentBinding.badgeStatus.text = getString(R.string.active)
        badgeFragmentBinding.badgeCompanyName.text = AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_COMPANY_NAME)
        badgeFragmentBinding.badgeExpires.text = AppSharedPreferences.readString(sharedPreferences, Constants.APPOINT_DATE)
    }

    private fun setQrCodeImage() {
        badgeFragmentBinding.badgeQrCode.setImageBitmap(
            Utils.QRCodeGenerator(AppSharedPreferences.readString(sharedPreferences, Constants.VENDOR_GUID),
                340, 340))
    }

    private fun setAppointmentStatus() {
        badgeFragmentBinding.badgeAppt.text =  String.format(getString(R.string.appt),
            AppSharedPreferences.readString(sharedPreferences, Constants.APPOINT_TIME))
    }

    private fun setBadgeStatus() {
        badgeViewModel?.init(context)
        onBadgeConnectionStatus()
        badgeViewModel?.getBattery()
        badgeViewModel?.batteryLevel?.observe(viewLifecycleOwner) {
            onGetBattery(it)
        }
        onManageBadge()
    }

    private fun onBadgeConnectionStatus() {
        onBadgeConnectionStatusUpdate(BadgeController.getInstance().connectionState.value)
        badgeViewModel?.badgeConnectionStatus?.observe(viewLifecycleOwner) {
            onBadgeConnectionStatusUpdate(it)
        }
    }

    private fun onBadgeConnectionStatusUpdate(connectionStatus : Int) {
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
            }
        }
    }

    private fun onGetBattery(batteryLevel : Int) {
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
    }

    private fun onManageBadge() {
        badgeFragmentBinding.manageBadge.setOnClickListener {
            badgeViewModel?.onClose()
            findNavController().navigate(R.id.badgeManageFragment)
        }
    }
}