package com.certify.vendor.repo

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

    fun getAppointments(vendorId: Int, onResult: (isSuccess: Boolean, appointmentResponse: GetAppointmentResponse?) -> Unit) {
        val appointmentRequest = GetAppointmentRequest(
            Constants.VENDOR_APP, 0,1)
        RetrofitInstance.apiInterface.getAppointments(appointmentRequest)
            .enqueue(object : Callback<GetAppointmentResponse> {
                override fun onResponse(call: Call<GetAppointmentResponse>, response: Response<GetAppointmentResponse>) {
                    AppointmentDataSource.setUnauthorized(response.code())
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
                    onResult(false, null)
                }

            })
    }
    fun getPastAppointments(vendorId: Int, onResult: ( appointmentResponse: GetAppointmentResponse?) -> Unit) {
        val appointmentRequest = GetAppointmentRequest(
            Constants.VENDOR_APP, 0,2)
        RetrofitInstance.apiInterface.getAppointments(appointmentRequest)
            .enqueue(object : Callback<GetAppointmentResponse> {
                override fun onResponse(call: Call<GetAppointmentResponse>, response: Response<GetAppointmentResponse>) {
                        onResult( response.body())
                }

                override fun onFailure(call: Call<GetAppointmentResponse>, t: Throwable) {
                    onResult( null)
                }

            })
    }
}