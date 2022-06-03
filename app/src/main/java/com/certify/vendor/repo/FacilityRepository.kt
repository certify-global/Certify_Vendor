package com.certify.vendor.repo

import android.util.Log
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.request.DepartmentLocationRequest
import com.certify.vendor.api.response.DepartmentLocationResponse
import com.certify.vendor.api.response.FacilityResponse
import com.certify.vendor.data.FacilityDataSource
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
    fun getDepartmentLocationWith(facilityId: Int,onResult: (isSuccess: Boolean, facilityResponse: DepartmentLocationResponse?) -> Unit) {

        val departmentLocationRequest =DepartmentLocationRequest(facilityId)
        RetrofitInstance.apiInterface.getDepartmentLocationWith(departmentLocationRequest).enqueue(object : Callback<DepartmentLocationResponse> {
                override fun onResponse(call: Call<DepartmentLocationResponse>, response: Response<DepartmentLocationResponse>) {
                    Log.d(TAG, "getDepartmentLocationWith " + response.body())
                    if (response.body()?.responseCode == 1) {
                        onResult(true, response.body())
                    } else {
                        onResult(false, response.body())
                    }
                }

                override fun onFailure(call: Call<DepartmentLocationResponse>, t: Throwable) {
                    Log.e(TAG, "Error in Get Appointments")
                    onResult(false, null)
                }

            })
    }
}