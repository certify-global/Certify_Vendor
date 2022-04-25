package com.certify.vendor.badge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.certify.vendor.R
import com.netronix.ble.connect.BLEManager
import com.netronix.ble.connect.OTAManager.BluetoothGattSingleton
import com.netronix.ble.connect.data.File
import com.netronix.ble.connect.data.Statics
import com.netronix.ebadge.inc.IntentsDefined

object BadgeFirmwareUpdate {

    private val TAG = BadgeFirmwareUpdate::class.java.simpleName
    private var badgeArg : IntentsDefined.BadgeArgument ? = null
    const val FIRMWARE_VERSION1 = "0.3.23"
    const val FIRMWARE_VERSION2 = "0.4.01"
    private const val FIRMWARE_FILE_NAME = "h40d01_ota_nb_v0401.img"
    private var mBroadcastReceiver : EBadgeBroadcastReceiver? = null
    var fwUpdateProgress = MutableLiveData<Int>()

    fun writeFirmwareOTA(context: Context?, badge : IntentsDefined.BadgeArgument) {
        badgeArg = badge
        initFirmwareUpdate(context)
    }

    private fun initFirmwareUpdate(context: Context?) {
        Log.d(TAG, "Firmware Update init")
        mBroadcastReceiver = EBadgeBroadcastReceiver()
        mBroadcastReceiver?.register(context)

        BluetoothGattSingleton.setBluetoochManager(BLEManager.getOTAManager())
        badgeArg?.fwVersion = FIRMWARE_VERSION2
        //BLEManager.getOTAManager().file = File(context?.assets?.open(FIRMWARE_FILE_NAME))
        BLEManager.getOTAManager().fileName = FIRMWARE_FILE_NAME
        BLEManager.getOTAManager().file = File(context?.resources?.openRawResource(R.raw.h40d01_ota_nb_v0401))
        initOTAValue()
        BLEManager.getOTAManager().formatResult = true
    }

    private fun initOTAValue() {
        BLEManager.getOTAManager().setMemoryType(Statics.MEMORY_TYPE_SPI)
        BLEManager.getOTAManager().setMISO_GPIO(Statics.DEFAULT_MISO_VALUE)
        BLEManager.getOTAManager().setMOSI_GPIO(Statics.DEFAULT_MOSI_VALUE)
        BLEManager.getOTAManager().setCS_GPIO(Statics.DEFAULT_CS_VALUE)
        BLEManager.getOTAManager().setSCK_GPIO(Statics.DEFAULT_SCK_VALUE)
        BLEManager.getOTAManager()
            .setFileBlockSize(Integer.valueOf(Statics.DEFAULT_BLOCK_SIZE_VALUE))
        BLEManager.getOTAManager().setImageBank(Statics.DEFAULT_MEMORY_BANK)
    }

    private fun startUpdate() {
        Log.d(TAG, "Firmware Start update")
        val intent = Intent()
        intent.action = Statics.BLUETOOTH_GATT_UPDATE
        intent.putExtra("step", 1)
        BLEManager.getOTAManager().processStep(intent)
    }

    internal class EBadgeBroadcastReceiver : BroadcastReceiver() {
        var mIsRegistered = false
        var context : Context? = null

        fun register(context: Context?) {
            this.context = context
            val intentFilter = IntentFilter()
            intentFilter.addAction(IntentsDefined.Action.Response.toString())
            intentFilter.addAction(IntentsDefined.Action.ReportStatus.toString())
            intentFilter.addAction(IntentsDefined.Action.ReportGetData.toString())
            intentFilter.addAction(IntentsDefined.Action.ReportScanDevice.toString())
            intentFilter.addAction(IntentsDefined.Action.ReportWriteProgress.toString())
            intentFilter.addAction(IntentsDefined.Action.ReportWriteOTAProgress.toString())
            intentFilter.addAction(IntentsDefined.Action.ReportOTAStep.toString())
            intentFilter.addAction(Statics.BLUETOOTH_GATT_UPDATE)
            context?.registerReceiver(mBroadcastReceiver, intentFilter)
            mIsRegistered = true
            Log.i(TAG, "EBadgeBroadcastReceiver registered !")
        }

        fun unRegister(context: Context?) {
            context?.unregisterReceiver(mBroadcastReceiver)
            mIsRegistered = false
            Log.i(TAG, "EBadgeBroadcastReceiver unregistered !")
        }

        override fun onReceive(context: Context?, intent: Intent) {
            try {
                val action = intent.action
                Log.d(TAG, "Badge intent action EBadge " + intent.action)
                when (action) {
                    IntentsDefined.Action.Response.toString() -> { //Response of command
                        val err = intent.getIntExtra(IntentsDefined.ExtraName.ErrorCode.toString(), -1)
                        val errCode = IntentsDefined.ErrorCode.getErrorCode(err)
                        Log.i(TAG, "onReceived : Response[$errCode]")
                        if (errCode == IntentsDefined.ErrorCode.Start_ota_action) {
                            startUpdate()
                        }
                    }
                    IntentsDefined.Action.ReportStatus.toString() -> { //return the connectiong Status between app and device
                        val status = intent.getIntExtra(IntentsDefined.ExtraName.Status.toString(), -1)
                        Log.i(TAG, "onReceived : ReportStatus = $status")
                    }
                    IntentsDefined.Action.ReportGetData.toString() -> {
                        val getData = intent.getStringExtra(IntentsDefined.ExtraName.GetData.toString())
                        val getCmdAction =
                            intent.getStringExtra(IntentsDefined.ExtraName.GetCmdAction.toString())
                        val reportData = String.format("$getCmdAction:$getData")
                        Log.i(
                            TAG,
                            "onReceived : ReportGetData getData = $getData"
                        )
                    }
                    IntentsDefined.Action.ReportWriteProgress.toString() -> {
                        val percent = intent.getIntExtra(IntentsDefined.ExtraName.WriteProgress.toString(), -1);
                        Log.d(TAG, "Update percent $percent")
                    }
                    IntentsDefined.Action.ReportWriteOTAProgress.toString() -> {
                        val otaPercent = intent.getIntExtra(IntentsDefined.ExtraName.WriteOTAProgress.toString(), -1);
                        Log.d(TAG, "Update percent $otaPercent")
                        fwUpdateProgress.value = otaPercent
                    }
                    IntentsDefined.Action.ReportOTAStep.toString() -> {
                        val otaResult = intent.getIntExtra(IntentsDefined.ExtraName.ReportOTAResult.toString(), -1);
                        Log.d(TAG, "Update percent $otaResult")
                        if (otaResult == Statics.OTA_STEP_ON_SUCCESS) {
                            onFirmwareUpdateComplete()
                        }
                    }
                    Statics.BLUETOOTH_GATT_UPDATE -> {
                        val otaStep = intent.getIntExtra("step", -1)
                        Log.d(TAG, "Update percent $otaStep")
                        BLEManager.getOTAManager().processStep(intent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun onFirmwareUpdateComplete() {
        Log.d(TAG, "Firmware update success")
        BLEManager.getOTAManager().sendRebootSignal()
        badgeArg?.forceOTA = false;
        fwUpdateProgress.value = 101;
        unregisterReceiver()
    }

    fun unregisterReceiver() {
        mBroadcastReceiver?.unRegister(mBroadcastReceiver?.context)
    }
}