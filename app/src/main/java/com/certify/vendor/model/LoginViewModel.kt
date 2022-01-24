package com.certify.vendor.model

import android.util.Log
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    fun login() {
        Log.d("LoginViewModel", "Login")
    }
}