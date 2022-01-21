package com.certify.vendor.badge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.certify.vendor.ble.BtDevScanInfo
import com.netronix.ble.connect.BLEManager
import com.netronix.ble.connect.data.Statics
import com.netronix.ble.connect.dither.Common
import com.netronix.ble.scan.BleDevice
import com.netronix.ble.scan.BleScanCallback
import com.netronix.ble.scan.BleScanConfig
import com.netronix.ble.scan.BleScanProc
import com.netronix.ebadge.inc.IntentsDefined
import java.util.*

object Badge {
    private val TAG : String ?= Badge::class.simpleName

    private var badgeArg : IntentsDefined.BadgeArgument? = IntentsDefined.BadgeArgument()
    private var bleScanProc : BleScanProc? = null
    private var eBadgeReceiver : EbadgeBroadcastReceiver = EbadgeBroadcastReceiver()
    private var scanConfig : ScanConfig? = null

    public fun init(context : Context) {
        bleScanProc = BleScanProc()
        badgeArg?.setFlavor(IntentsDefined.ProductFlavor.NB.id)
        BLEManager.start(context)
        eBadgeReceiver.register(context)
        scanConfig = ScanConfig(context)
        startScan(context)
    }

    private fun startScan(context: Context) {
        bleScanProc?.startScan(context, bleScanCallback)
    }

    var bleScanCallback : BleScanCallback = object : BleScanCallback {
        override fun onLeScan(bleDevice:  BleDevice?, rssi: Int, scanRecord: ByteArray?) {
            Log.d (TAG, "Scan result " + rssi)
            val btDevScanInfo = BtDevScanInfo (bleDevice, rssi, scanRecord)
        }

        override fun onScanFailed(p0: BleScanCallback.ERR_SCAN?) {
            TODO("Not yet implemented")
        }

        override fun onScanApiSwitch(p0: BleScanConfig.ScanApi?) {
            TODO("Not yet implemented")
        }

        override fun getBleScanConfig(): BleScanConfig {
            return scanConfig!!
        }

        override fun getOnScanCheckNamePrefix(): String {
            TODO("Not yet implemented")
        }

        override fun getOnScanCheckServiceUUIDs(): MutableList<UUID> {
            return Common.gBleDevInfo.broadcastCheckUUIDs
        }
    }

    class EbadgeBroadcastReceiver : BroadcastReceiver() {

        fun register(context: Context?) {
            val intent = IntentFilter()
            intent.addAction(IntentsDefined.Action.Response.toString())
            intent.addAction(IntentsDefined.Action.ReportStatus.toString())
            intent.addAction(IntentsDefined.Action.ReportGetData.toString())
            intent.addAction(IntentsDefined.Action.ReportScanDevice.toString())
            intent.addAction(IntentsDefined.Action.ReportWriteProgress.toString())
            intent.addAction(IntentsDefined.Action.ReportWriteOTAProgress.toString())
            intent.addAction(IntentsDefined.Action.ReportOTAStep.toString())
            intent.addAction(Statics.BLUETOOTH_GATT_UPDATE)

            context?.registerReceiver(this, intent)
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            TODO("Not yet implemented")
        }
    }

    class ScanConfig(private var context: Context?) : BleScanConfig.Default() {

        override fun getContext(): Context? {
            return context;
        }

        override fun getScanApi(): ScanApi {
            return ScanApi.API_21
        }
    }
}