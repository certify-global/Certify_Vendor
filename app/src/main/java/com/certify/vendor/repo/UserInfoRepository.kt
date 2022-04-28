package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.UpdateUserRequest
import com.certify.vendor.api.response.UpdateUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfoRepository {
    private val TAG = UserInfoRepository::class.java.simpleName

    fun updateUserInfo(badgeMacAddress : String, onResult: (isSuccess: Boolean) -> Unit) {
        val updateUserRequest = UpdateUserRequest(badgeMacAddress)
        RetrofitInstance.apiInterface.updateUserInfo(updateUserRequest).enqueue(object :
            Callback<UpdateUserResponse> {
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                Log.d(TAG, "Update User Success $response")
                if (response.body()?.responseCode == 1) {
                    onResult(true)
                } else {
                    Log.d(TAG, "Update User Error " + response.body()?.responseMessage)
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                Log.e(TAG, "Error in update user "+ t.message)
                onResult(false)
            }
        })
    }
}