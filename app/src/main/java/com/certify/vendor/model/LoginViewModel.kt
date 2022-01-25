package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.VendorApplication
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.common.Utils
import com.certify.vendor.repo.LoginRepository

class LoginViewModel : ViewModel() {

    val signInLiveData = MutableLiveData<Boolean>()
    private var loginRepository : LoginRepository = LoginRepository()

    fun init(context: Context?) {
        RetrofitInstance.init(context)
    }

    fun login(userName : String, password : String) {
        loginRepository.signIn(userName, Utils.encodeToBase64(password)) { isSuccess, response ->
            if (isSuccess) {
                VendorApplication.isLoggedIn = true
            }
            signInLiveData.value = isSuccess
        }
    }
}