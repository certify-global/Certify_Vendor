package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.ScheduleAppointmentRequest
import com.certify.vendor.api.response.ScheduleAppointmentResponse
import com.certify.vendor.common.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleAppoinmentRepository {
    companion object {
        private val TAG = ScheduleAppoinmentRepository::class.java.name
    }

    fun scheduleAppoinments(selectedDate: String,
                            startTime: String, endTime: String, contactName: Int,visitReason: String
                            ,facilityID:Int,locationId:Int, departmentId:Int,onResult: (isSuccess: Boolean, scheduleAppointmentResponse: ScheduleAppointmentResponse?) -> Unit) {

        val scheduleAppointmentRequest = ScheduleAppointmentRequest(facilityID, Constants.VENDOR_APP, selectedDate, startTime, endTime, contactName,visitReason,locationId,departmentId)

        RetrofitInstance.apiInterface.scheduleAppoinments(scheduleAppointmentRequest)
            .enqueue(object : Callback<ScheduleAppointmentResponse> {
                override fun onResponse(
                    call: Call<ScheduleAppointmentResponse>,
                    response: Response<ScheduleAppointmentResponse>
                ) {
                    Log.d(TAG, "Schedule Appointments responseCode " + response.body())
                    if (response.body()?.responseCode == 1) {

                        onResult(true, response.body())
                    } else {
                        onResult(false, response.body())
                    }
                }

                override fun onFailure(call: Call<ScheduleAppointmentResponse>, t: Throwable) {
                    Log.e(TAG, "Error in Schedule Appointments")
                    onResult(false, null)
                }

            })
    }
}