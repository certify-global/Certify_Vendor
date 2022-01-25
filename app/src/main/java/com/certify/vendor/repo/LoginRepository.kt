package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.LoginRequest
import com.certify.vendor.api.response.LoginResponse
import com.certify.vendor.common.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {
    private val TAG : String = LoginRepository::class.java.name

    fun signIn(userName : String, passwd : String, onResult: (isSuccess: Boolean, response: LoginResponse?) -> Unit) {
        val loginRequest = LoginRequest(0, 0, 0, 0, Constants.VENDOR_APP, userName, passwd, 0)

        RetrofitInstance.apiInterface.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d(TAG, "onLogin Success " + response.body()?.responseCode)
                if (response.body()?.responseCode == 1) {
                    onResult(true, response.body())
                } else {
                    onResult(true, response.body())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "Error in login")
                onResult(false, null)
            }
        })
    }

}