package com.certify.vendor.api.request

import java.io.Serializable

data class LoginRequest (val source : String, val email : String, val password : String, val VendorType : Int) : Serializable