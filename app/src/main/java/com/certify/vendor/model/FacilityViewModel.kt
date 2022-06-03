package com.certify.vendor.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.response.DepartmentLocationResponse
import com.certify.vendor.repo.FacilityRepository

class FacilityViewModel : BaseViewModel() {

    val facilityLiveData = MutableLiveData<Boolean>()
    val departmentLocationWithLiveData = MutableLiveData<DepartmentLocationResponse>()

    private var facilityRepository : FacilityRepository = FacilityRepository()

    fun init(context: Context?) {
        RetrofitInstance.init(context)
    }

    fun facility(vendorID:Int) {
        loading.value = true
        facilityRepository.getFacility(vendorID) { isSuccess, response ->
            loading.value = false
            facilityLiveData.value = isSuccess
        }
    }
    fun departmentLocationWith(facilityID:Int) {
        loading.value = true
        facilityRepository.getDepartmentLocationWith(facilityID) { isSuccess, response ->
            loading.value = false
            departmentLocationWithLiveData.value = response
        }
    }
}