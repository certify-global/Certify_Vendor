package com.certify.vendor.common

import android.util.Base64

class Utils {

    companion object {

        fun encodeToBase64(data : String): String = Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)
        
    }
}