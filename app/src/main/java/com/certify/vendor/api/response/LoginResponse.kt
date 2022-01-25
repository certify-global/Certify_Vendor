package com.certify.vendor.api.response

data class LoginResponse (val responseCode : Int, val responseSubCode : Int, val responseMessage : String?,
                          val responseData : LoginResponseData?, val errorMessage : String?)


data class LoginResponseData(val message : String?, val status : Int, val isActive : Int, val isLocked : Int, val vendorId : Int,
                            val userName : String?, val password : String?, val userEmail : String?, val token : String?,
                            val lastLoginDateTime : String, val firstName : String, val lastName : String, val vendorCompanyName : String,
                            val phone : String, val badgeId : String?, val badgeExpiry : String?, val profilePhotoData : String?)