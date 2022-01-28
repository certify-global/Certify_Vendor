package com.certify.vendor.data

import com.certify.vendor.api.response.LoginResponseData
import com.certify.vendor.api.response.ProfileData
import com.google.gson.Gson

object LoginDataSource {

    var loginData : LoginResponseData? = null
    var userProfilePicEncoded : String? = ""

    fun getUserProfileEncodedData() {
        if (loginData?.profilePhotoData?.isNotEmpty() == true) {
            val profileData : ProfileData? = Gson().fromJson(loginData?.profilePhotoData, ProfileData::class.java)
            userProfilePicEncoded = profileData?.FileData
        }
    }

}