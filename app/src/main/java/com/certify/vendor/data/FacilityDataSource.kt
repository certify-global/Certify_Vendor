package com.certify.vendor.data

import com.certify.vendor.api.response.FacilityData

object FacilityDataSource {

    private var facilitytList = arrayListOf<FacilityData>()

    fun addFacilityList(/*facilityData:  List<FacilityData>*/) {
        facilitytList.clear()
        val user0 = FacilityData("0", "Select Facility")
        val user1 = FacilityData("1", "John")
        val user2 = FacilityData("2", "Mary")
        val user3 = FacilityData("3", "Patrick")
        val user4 = FacilityData("4", "Amanda")
        val list = arrayListOf<FacilityData>(user0,user1, user2, user3, user4)
        facilitytList.addAll(list)
    }

    fun getFacilityList() = facilitytList
}