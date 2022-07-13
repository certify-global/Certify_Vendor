package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.EditVendorAppointmentRequest
import com.certify.vendor.api.request.UpdateAppointmentRequest
import com.certify.vendor.api.response.DataResponse
import com.certify.vendor.api.response.UpdateAppointmentResponse
import com.certify.vendor.common.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateAppointmentRepository {
    companion object {
        private val TAG = UpdateAppointmentRepository::class.java.name
    }

    fun updateAppointment(
        selectedDate: String,
        startTime: String, endTime: String, visitReason: String, appointmentId: Int, action: Int, facilityID: Int, onResult: (isSuccess: Boolean, updateAppointmentResponse: UpdateAppointmentResponse?) -> Unit
    ) {

        val updateAppointmentRequest = UpdateAppointmentRequest(
            facilityID, Constants.VENDOR_APP, startTime, endTime, visitReason, appointmentId, action
        )
        Log.d(TAG, "update Appointments request " + updateAppointmentRequest)

        RetrofitInstance.apiInterface.updateAppointment(updateAppointmentRequest)
            .enqueue(object : Callback<UpdateAppointmentResponse> {
                override fun onResponse(
                    call: Call<UpdateAppointmentResponse>,
                    response: Response<UpdateAppointmentResponse>
                ) {
                    Log.d(TAG, "update Appointments responseCode " + response.body())
                    if (response.body()?.responseCode == 1) {

                        onResult(true, response.body())
                    } else {
                        onResult(false, response.body())
                    }
                }

                override fun onFailure(call: Call<UpdateAppointmentResponse>, t: Throwable) {
                    Log.e(TAG, "Error in UpdateAppointmentResponse")
                    onResult(false, null)
                }

            })
    }

    fun editVendorAppointment(appointmentId: Int, contactId: Int, visitReason: String, departmentId: Int, onResult: (isSuccess: Boolean, dataResponse: DataResponse?) -> Unit) {

        val updateAppointmentRequest = EditVendorAppointmentRequest(
            appointmentId, visitReason, contactId, departmentId
        )
        Log.d(TAG, "update Appointments request " + updateAppointmentRequest)
        RetrofitInstance.apiInterface.updateVendorAppointment(updateAppointmentRequest)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                    Log.d(TAG, "update Appointments responseCode " + response.body())
                    onResult(true, response.body())
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    Log.e(TAG, "Error in UpdateAppointmentResponse")
                    onResult(false, null)
                }

            })
    }
}