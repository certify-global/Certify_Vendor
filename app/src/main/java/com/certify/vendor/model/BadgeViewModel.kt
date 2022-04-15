package com.certify.vendor.model

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.badge.BadgeController
import com.netronix.ble.connect.BLEManager

class BadgeViewModel : ViewModel(), BadgeController.BadgeListener {

    private val TAG = BadgeViewModel::class.java.simpleName
    var badgeConnectionStatus = MutableLiveData<Int>()
    var batteryLevel = MutableLiveData<Int>()

    fun init (context: Context?) {
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

    override fun onBadgeConnectionStatus(status: Int) {
        Log.d(TAG, "Badge Connection status $status")
        badgeConnectionStatus.value = status
    }

    override fun onBadgeGetBattery(bLevel: Int) {
        batteryLevel.value = bLevel
    }

    fun onClose() {
        BadgeController.getInstance().onBadgeClose()
    }
}