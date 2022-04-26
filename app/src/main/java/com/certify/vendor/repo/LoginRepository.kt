package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.VendorApplication
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.LoginRequest
import com.certify.vendor.api.response.LoginResponse
import com.certify.vendor.common.Constants
import com.certify.vendor.data.LoginDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {
    private val TAG : String = LoginRepository::class.java.name

    fun signIn(userName : String, passwd : String, onResult: (isSuccess: Boolean, response: LoginResponse?) -> Unit) {
        val loginRequest = LoginRequest(Constants.VENDOR_APP, userName, passwd, 2)
        Log.d(TAG, "Login loginRequest $loginRequest")
        RetrofitInstance.apiInterface.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d(TAG, "Login Success $response")
                if (response.body()?.responseCode == 1) {
                    VendorApplication.accessToken = response.body()?.responseData?.token
                    LoginDataSource.loginData = response.body()?.responseData
                    LoginDataSource.getUserProfileEncodedData()
                    onResult(true, response.body())
                } else {
                    Log.d(TAG, "Login Error " + response.body()?.responseMessage)
                    onResult(false, response.body())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(TAG, "Error in login"+t.message)
                onResult(false, null)
            }
        })
    }

}