package com.certify.vendor.model

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.badge.BadgeController
import com.certify.vendor.badge.BadgeFirmwareUpdate
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.repo.UserInfoRepository

class BadgeViewModel : ViewModel(), BadgeController.BadgeListener {

    private val TAG = BadgeViewModel::class.java.simpleName
    var badgeConnectionStatus = MutableLiveData<Int>()
    var batteryLevel = MutableLiveData<Int>()
    val firmwareProgress = MutableLiveData<Int>()
    var badgeAvailable = MutableLiveData<Boolean>()
    var context : Context? = null

    fun init (context: Context?) {
        this.context = context
        BadgeController.getInstance().initBle(context)
        BadgeController.getInstance().setListener(this)
    }

    fun connect(badgeDevice: BluetoothDevice?) {
        Log.d(TAG, "Initiate connection")
        BadgeController.getInstance().stopScan()
        BadgeController.getInstance().connectDevice(badgeDevice)
    }

    fun getBattery() {
        BadgeController.getInstance().getBattery()
    }

    fun writeBadgeFirmware (lifecycleOwner: LifecycleOwner) {
        BadgeController.getInstance().setListener(this)
        BadgeController.getInstance().writeFirmwareOTA()
        BadgeFirmwareUpdate.fwUpdateProgress.observe(lifecycleOwner) {
            if (it == 101) {
                val sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
                AppSharedPreferences.writeSp(sharedPreferences, Constants.BADGE_FIRMWARE_VERSION, BadgeFirmwareUpdate.FIRMWARE_VERSION2)
                AppSharedPreferences.writeSp(sharedPreferences, Constants.BADGE_FW_UPDATE_TIME,
                    Utils.getCurrentDate("MMMM dd, yyyy") + " " + Utils.getCurrentTime())
            }
            firmwareProgress.value = it
        }
    }

    fun isBadgeFirmwareUpdateToDate(context: Context?) : Boolean {
        val firmwareVersion = AppSharedPreferences.getSharedPreferences(context)
            ?.getString(Constants.BADGE_FIRMWARE_VERSION, BadgeFirmwareUpdate.FIRMWARE_VERSION1)
        return (firmwareVersion.equals(BadgeFirmwareUpdate.FIRMWARE_VERSION2))
    }

    override fun onBadgeConnectionStatus(status: Int, bleDevice: BluetoothDevice) {
        Log.d(TAG, "Badge Connection status $status")
        badgeConnectionStatus.value = status
        if (status == BadgeController.BadgeConnectionState.CONNECTED.value) {
            val sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
            var badgeMacAddress = sharedPreferences?.getString(Constants.BADGE_MAC_ADDRESS, "")
            if (badgeMacAddress.isNullOrEmpty()) {
                badgeMacAddress = bleDevice.address
                val userInfoRepository = UserInfoRepository()
                userInfoRepository.updateUserInfo(badgeMacAddress) { response ->
                    if (response) {
                        AppSharedPreferences.writeSp(
                            sharedPreferences,
                            Constants.BADGE_MAC_ADDRESS,
                            badgeMacAddress
                        )
                    }
                }
            }
        }
    }

    override fun onBadgeGetBattery(bLevel: Int) {
        batteryLevel.value = bLevel
    }

    override fun onGetFirmwareVersion(version: String?) {
        val sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
        AppSharedPreferences.writeSp(sharedPreferences, Constants.BADGE_FIRMWARE_VERSION, version)
    }

    override fun onBadgeUnavailable() {
        badgeAvailable.value = true
    }

    fun onClose() {
        BadgeController.getInstance().onBadgeClose()
    }
}