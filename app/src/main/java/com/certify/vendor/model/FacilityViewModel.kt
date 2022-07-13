package com.certify.vendor.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.response.DepartmentLocationResponse
import com.certify.vendor.api.response.GetFacilityMembersResponse
import com.certify.vendor.repo.FacilityRepository

class FacilityViewModel : BaseViewModel() {

    val facilityLiveData = MutableLiveData<Boolean>()
    val departmentLocationWithLiveData = MutableLiveData<DepartmentLocationResponse>()
    val facilityMembersRequestWithLiveData = MutableLiveData<GetFacilityMembersResponse>()


    private var facilityRepository: FacilityRepository = FacilityRepository()

    fun init(context: Context?) {
        RetrofitInstance.init(context)
    }

    fun facility(vendorID: Int) {
        loading.value = true
        facilityRepository.getFacility(vendorID) { isSuccess, response ->
            loading.value = false
            facilityLiveData.value = isSuccess
        }
    }

    fun departmentLocationWith(facilityID: Int) {
        loading.value = true
        facilityRepository.getDepartmentLocationWith(facilityID) { isSuccess, response ->
            loading.value = false
            departmentLocationWithLiveData.value = response
        }
    }

    fun getOnSiteContact(facilityID: Int, memberName: String) {

        facilityRepository.getFacilityMembers(facilityID, memberName) { isSuccess, response ->
            Log.i("WithLiveData Members = ", "" + response)
            facilityMembersRequestWithLiveData.value = response
        }
    }

}