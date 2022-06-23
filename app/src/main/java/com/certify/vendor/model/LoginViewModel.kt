package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.VendorApplication
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.repo.LoginRepository

class LoginViewModel : BaseViewModel() {

    val signInLiveData = MutableLiveData<Boolean>()
    val responseMessage = MutableLiveData<String>()
    private var loginRepository : LoginRepository = LoginRepository()
    private var context : Context? = null

    fun init(context: Context?) {
        this.context = context
        RetrofitInstance.init(context)
    }

    fun login(userName : String, password : String) {
        loading.value = true
        loginRepository.signIn(userName, Utils.encodeToBase64(password)) { isSuccess, response ->
            loading.value = false
            if (isSuccess) {
                VendorApplication.isLoggedIn = true

                val sharedPreferences = AppSharedPreferences.getSharedPreferences(context)
                AppSharedPreferences.writeSp(sharedPreferences, Constants.ACCESS_TOKEN, response?.responseData?.token)
                AppSharedPreferences.writeSp(
                    sharedPreferences, Constants.BADGE_MAC_ADDRESS,
                    response?.responseData?.badgeMacAddress
                )
                AppSharedPreferences.writeSp(
                    AppSharedPreferences.getSharedPreferences(context), Constants.VENDOR_GUID,
                    response?.responseData?.vendorGuid
                )
            }else{
                responseMessage.value = response?.responseMessage!!
            }
            signInLiveData.value = isSuccess
        }
    }
}