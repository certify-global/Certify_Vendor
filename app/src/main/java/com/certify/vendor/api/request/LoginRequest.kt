package com.certify.vendor.api.request

import java.io.Serializable

data class LoginRequest (val vendorId : Int, val certifyId : Int, val facilityId : Int, val userId : Int,
                         val source : String, val email : String, val password : String, val src_Vc_Id : Int, val VendorType : Int) : Serializable