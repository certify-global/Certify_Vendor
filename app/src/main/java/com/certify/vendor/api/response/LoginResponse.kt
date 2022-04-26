package com.certify.vendor.api.response

data class LoginResponse (val responseCode : Int, val responseSubCode : Int, val responseMessage : String?,
                          val responseData : LoginResponseData?, val errorMessage : String?)


data class LoginResponseData(val message : String?, val status : Int, val isActive : Int, val isLocked : Int, val vendorId : Int,
                            val userName : String?, val password : String?, val userEmail : String?, val token : String?,
                            val lastLoginDateTime : String, val firstName : String, val lastName : String, val vendorCompanyName : String,
                            val phone : String, val badgeId : String?, val badgeExpiry : String?, val badgeMacAddress : String?,
                            val address1 : String?, val city : String?, val state : String?, val country : String?, val zipcode : String,
                            val companyId : String?, val contactName : String?, val certifyId : String?, val profilePhotoData : String?)

data class ProfileData(val FileData : String?, val FileName : String?)