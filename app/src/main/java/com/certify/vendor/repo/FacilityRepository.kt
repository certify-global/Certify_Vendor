package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.GetAppointmentRequest
import com.certify.vendor.api.response.FacilityResponse
import com.certify.vendor.api.response.GetAppointmentResponse
import com.certify.vendor.common.Constants
import com.certify.vendor.data.AppointmentDataSource
import com.certify.vendor.data.FacilityDataSource
import com.certify.vendor.data.LoginDataSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FacilityRepository {
    companion object {
        private val TAG = FacilityRepository::class.java.name
    }

    fun getFacility(vendorId: Int,onResult: (isSuccess: Boolean, facilityResponse: FacilityResponse?) -> Unit) {

        RetrofitInstance.apiInterface.getFacilityList(vendorId)
            .enqueue(object : Callback<FacilityResponse> {
                override fun onResponse(
                    call: Call<FacilityResponse>,
                    response: Response<FacilityResponse>
                ) {
                    Log.d(TAG, "Get Facilities responseCode " + response.body())
                    if (response.body()?.responseCode == 1) {
                        if (response.body()?.responseData?.isNotEmpty() == true) {
                            FacilityDataSource.addFacilityList(response.body()?.responseData!!)
                        }
                        onResult(true, response.body())
                    } else {
                        onResult(false, response.body())
                    }
                }

                override fun onFailure(call: Call<FacilityResponse>, t: Throwable) {
                    Log.e(TAG, "Error in Get Appointments")
                    onResult(false, null)
                }

            })
    }
}