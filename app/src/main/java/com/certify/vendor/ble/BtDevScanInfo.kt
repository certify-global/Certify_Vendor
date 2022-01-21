package com.certify.vendor.ble

import com.netronix.ble.scan.BleDevice

data class BtDevScanInfo (val bleDevice : BleDevice?, val rssi : Int, val scanRecord : ByteArray?)