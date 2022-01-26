package com.certify.vendor.common

import android.util.Base64
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        fun encodeToBase64(data: String): String = Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)

        fun getDate(inputData: String) : String {
            return inputData
        }
    }
}