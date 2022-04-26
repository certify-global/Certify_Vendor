package com.certify.vendor.data

import com.certify.vendor.api.response.FacilityData

object FacilityDataSource {

    private var facilitytList = arrayListOf<FacilityData>()

    fun addFacilityList(facilityData:  List<FacilityData>) {
        facilitytList.clear()
        val user0 = FacilityData(0, "Select Facility","","","","","","","","",
                                0, false, 0, "", 0)
        facilitytList.add(user0)
        facilitytList.addAll(facilityData)
    }

    fun getFacilityList() = facilitytList


}