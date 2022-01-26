package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.GetAppointmentRequest
import com.certify.vendor.api.response.GetAppointmentResponse
import com.certify.vendor.common.Constants
import com.certify.vendor.data.AppointmentDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentRepository {
    companion object {
        private val TAG = AppointmentRepository::class.java.name
    }

    fun getAppointments(onResult: (isSuccess: Boolean, appointmentResponse: GetAppointmentResponse?) -> Unit) {

        val appointmentRequest = GetAppointmentRequest(75, 0, 0, 0, Constants.VENDOR_APP,
                0, 0, 0, true)
        RetrofitInstance.apiInterface.getAppointments(appointmentRequest).enqueue(object : Callback<GetAppointmentResponse> {
            override fun onResponse(call: Call<GetAppointmentResponse>, response: Response<GetAppointmentResponse>) {
                Log.d(TAG, "Get Appointments " + response.body()?.responseCode)
                if (response.body()?.responseCode == 1) {
                    if (response.body()?.responseData?.isNotEmpty() == true) {
                        AppointmentDataSource.addAppointmentList(response.body()?.responseData!!)
                    }
                    onResult(true, response.body())
                } else {
                    onResult(false, response.body())
                }
            }

            override fun onFailure(call: Call<GetAppointmentResponse>, t: Throwable) {
                Log.e(TAG, "Error in Get Appointments")
                onResult(false, null)
            }

        })
    }
}