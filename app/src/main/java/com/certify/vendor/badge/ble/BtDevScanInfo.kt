package com.certify.vendor.badge.ble

import com.netronix.ble.scan.BleDevice

data class BtDevScanInfo (val bleDevice : BleDevice?, val rssi : Int, val scanRecord : ByteArray?)