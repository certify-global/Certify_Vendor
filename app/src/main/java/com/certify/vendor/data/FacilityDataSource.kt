package com.certify.vendor.data

import com.certify.vendor.api.response.FacilityData

object FacilityDataSource {

    private var facilitytList = arrayListOf<FacilityData>()

    fun addFacilityList(facilityData:  List<FacilityData>) {
        facilitytList.clear()
        facilitytList.addAll(facilityData)
    }
    fun getFacilityList() = facilitytList
}