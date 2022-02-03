package com.certify.vendor.model

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.certify.vendor.VendorApplication
import com.certify.vendor.api.RetrofitInstance
import com.certify.vendor.api.response.FacilityData
import com.certify.vendor.common.Constants
import com.certify.vendor.common.Utils
import com.certify.vendor.data.AppSharedPreferences
import com.certify.vendor.repo.FacilityRepository
import com.certify.vendor.repo.LoginRepository

class FacilityViewModel : BaseViewModel() {

    val facilityLiveData = MutableLiveData<Boolean>()
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

}