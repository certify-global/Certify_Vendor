package com.certify.vendor.data

import com.certify.vendor.api.response.DepartmentforfacilityList
import com.certify.vendor.api.response.FacilityData
import com.certify.vendor.api.response.LocationforfacilityList

object FacilityDataSource {

    private var facilitytList = arrayListOf<FacilityData>()
    private var departmentList = arrayListOf<DepartmentforfacilityList>()
    private var locationForFacilityList = arrayListOf<LocationforfacilityList>()


    fun addFacilityList(facilityData: List<FacilityData>) {
        facilitytList.clear()
        val user0 = FacilityData(
            0, "Select Facility", "", "", "", "", "", "", "", "",
            0, false, 0, "", 0
        )
        facilitytList.add(user0)
        facilitytList.addAll(facilityData)
    }

    fun getFacilityList() = facilitytList
    fun addDepartmentList(department: List<DepartmentforfacilityList>) {
        departmentList.clear()
        if (department.isEmpty()) return
        val user0 = DepartmentforfacilityList(0, "Select Department")
        departmentList.add(user0)
        departmentList.addAll(department)
    }

    fun getDepartmentList() = departmentList

    fun addLocationForFacilityList(facilityData: List<LocationforfacilityList>) {
        locationForFacilityList.clear()
        if (facilityData.isEmpty()) return
        val user0 = LocationforfacilityList(0, 0, "Select Location")
        locationForFacilityList.add(user0)
        locationForFacilityList.addAll(facilityData)
    }

    fun getLocationForFacilityList() = locationForFacilityList


}